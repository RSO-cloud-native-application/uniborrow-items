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
import org.eclipse.microprofile.metrics.annotation.Counted;
import org.eclipse.microprofile.metrics.annotation.Timed;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;

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
    @Timed(name = "get_items_time")
    @Operation(description = "Get items by filter, or all.", summary = "Get items by filter, or all.")
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "Items that fit the filter.",
                    content = @Content(schema = @Schema(implementation = ItemEntity.class, type = SchemaType.ARRAY))
            ),
            @APIResponse(
                    responseCode = "404",
                    description = "No items found."
            )
    })
    public Response getItems() {
        List<ItemEntity> items = itemBean.getItemsFilter(uriInfo);
        return Response.status(200).entity(items).build();
    }

    @GET
    @Operation(description = "Get item by id.", summary = "Get item by id.")
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "Item by id.",
                    content = @Content(schema = @Schema(implementation = ItemEntity.class))
            ),
            @APIResponse(
                    responseCode = "404",
                    description = "Item with id not found."
            )
    })
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
    @Counted(name = "num_created_items")
    @Operation(description = "Create new item.", summary = "Create new item.")
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "CreatedRequest",
                    content = @Content(schema = @Schema(implementation = ItemEntity.class))
            ),
            @APIResponse(
                    responseCode = "400",
                    description = "Problems with item body."
            )
    })
    public Response createItem(ItemEntity itemEntity) {
        if (itemEntity.getTitle() == null ||
                itemEntity.getDescription() == null || itemEntity.getUserId() == null
                || itemEntity.getCategory() == null) {
            return Response.status(300).build();
        } else {
            List<String> listOfTags = irs.getTags(itemEntity.getUri());
            if (listOfTags != null) {
                itemEntity.setDescription(itemEntity.getDescription() + "\n\nTags: " + listOfTags.stream().collect(Collectors.joining(", ")));
            }
            itemEntity.setStatus("Available");
            itemEntity = itemBean.createItem(itemEntity);
        }
        return Response.status(Response.Status.OK).entity(itemEntity).build();
    }

    @PUT
    @Path("{itemId}")
    @Operation(description = "Edit a item.", summary = "Edit a item.")
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "Edited item",
                    content = @Content(schema = @Schema(implementation = ItemEntity.class))
            ),
            @APIResponse(
                    responseCode = "400",
                    description = "Problems with item body."
            )
    })
    public Response updateItem(Item item, @PathParam("itemId") Integer itemId) {

        item = itemBean.putItem(item, itemId);
        if (item == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.status(Response.Status.OK).build();
    }

    @PATCH
    @Path("{itemId}")
    @Operation(description = "Patch a item.", summary = "Item a request.")
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "Patched item",
                    content = @Content(schema = @Schema(implementation = ItemEntity.class))
            ),
            @APIResponse(
                    responseCode = "400",
                    description = "Problems with item body."
            )
    })
    public Response patchItem(Item item, @PathParam("itemId") Integer itemId) {
        item = itemBean.patchItem(item, itemId);
        if (item == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.status(Response.Status.OK).build();
    }

    @DELETE
    @Counted(name = "num_deleted_items")
    @Path("{itemId}")
    @Operation(description = "Delete a item.", summary = "Delete a item.")
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "Item deleted."
            ),
            @APIResponse(
                    responseCode = "404",
                    description = "Item not found."
            )
    })
    public Response deleteItem(@PathParam("itemId") Integer itemId) {
        boolean isSuccessful = itemBean.deleteItem(itemId);
        if (isSuccessful) {
            return Response.status(Response.Status.NO_CONTENT).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }
}