package si.fri.rso.uniborrow.items.api.v1.resources;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.kumuluz.ee.logs.cdi.Log;

import si.fri.rso.uniborrow.items.lib.Item;
import si.fri.rso.uniborrow.items.models.entities.ItemEntity;
import si.fri.rso.uniborrow.items.services.beans.ItemBean;
import si.fri.rso.uniborrow.items.services.config.RestProperties;
import si.fri.rso.uniborrow.items.services.recognition.ImageRecognitionService;

@Log
@ApplicationScoped
@Path("/items")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)

public class ItemsResource {

    private final Logger log = Logger.getLogger(ItemsResource.class.getName());

    @Inject
    private ItemBean itemBean;

    @Inject
    private RestProperties rp;

    @Inject
    private ImageRecognitionService irs;

    @Context
    protected UriInfo uriInfo;

    @GET
    public Response getItems() {
        List<ItemEntity> items = itemBean.getItemsFilter(uriInfo);
        return Response.status(200).entity(items).build();
    }

    @GET
    @Path("/{itemId}")
    public Response getItem(@PathParam("itemId") Integer itemId) {
        Item item = itemBean.getItem(itemId);
        if (item == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        if (rp.getMaintenanceMode()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.status(Response.Status.OK).entity(item).build();
    }

    @POST
    public Response createItem(ItemEntity itemEntity) {
        if (itemEntity.getTitle() == null ||
                itemEntity.getDescription() == null || itemEntity.getUserId() == null
                || itemEntity.getCategory() == null) {
            return Response.status(300).build();
        } else {
            List<String> listOfTags = irs.getTags(itemEntity.getUri());
            itemEntity.setDescription(itemEntity.getDescription() + "\n\nTags: " + listOfTags.stream().collect(Collectors.joining(", ")));
            itemEntity.setStatus("Available");
            itemEntity = itemBean.createItem(itemEntity);
        }
        return Response.status(Response.Status.OK).entity(itemEntity).build();
    }

    @PUT
    @Path("{itemId}")
    public Response updateItem(Item item, @PathParam("itemId") Integer itemId) {

        item = itemBean.putItem(item, itemId);
        if (item == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.status(Response.Status.OK).build();
    }

    @PATCH
    @Path("{itemId}")
    public Response patchItem(Item item, @PathParam("itemId") Integer itemId) {
        item = itemBean.patchItem(item, itemId);
        if (item == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.status(Response.Status.OK).build();
    }

    @DELETE
    @Path("{itemId}")
    public Response deleteItem(@PathParam("itemId") Integer itemId) {
        boolean isSuccessful = itemBean.deleteItem(itemId);
        if (isSuccessful) {
            return Response.status(Response.Status.NO_CONTENT).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }
}