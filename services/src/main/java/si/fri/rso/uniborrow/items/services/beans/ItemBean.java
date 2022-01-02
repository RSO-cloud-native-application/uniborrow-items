package si.fri.rso.uniborrow.items.services.beans;

import com.kumuluz.ee.rest.beans.QueryParameters;
import com.kumuluz.ee.rest.utils.JPAUtils;
import si.fri.rso.uniborrow.items.lib.Item;
import si.fri.rso.uniborrow.items.models.converters.ItemConverter;
import si.fri.rso.uniborrow.items.models.entities.ItemEntity;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.ws.rs.NotFoundException;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;


import javax.annotation.PostConstruct;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.UriInfo;
import java.time.temporal.ChronoUnit;

@RequestScoped
public class ItemBean {

    private final Logger log = Logger.getLogger(ItemBean.class.getName());

    @Inject
    private EntityManager em;

    public List<Item> getItems() {
        TypedQuery<ItemEntity> query =
                em.createNamedQuery("ItemEntity.getAll", ItemEntity.class);
        List<ItemEntity> resultList = query.getResultList();
        return resultList.stream().map(ItemConverter::toDto).collect(Collectors.toList());
    }

    public List<ItemEntity> getItemsFilter(UriInfo uriInfo) {
        QueryParameters queryParameters = QueryParameters.query(uriInfo.getRequestUri().getQuery()).defaultOffset(0)
                .build();

        return JPAUtils.queryEntities(em, ItemEntity.class, queryParameters);
    }

    public Item getItem(Integer id) {
        ItemEntity itemEntity = em.find(ItemEntity.class, id);
        if (itemEntity == null) {
            throw new NotFoundException();
        }
        Item item = ItemConverter.toDto(itemEntity);
        return item;
    }

    public ItemEntity createItem(ItemEntity itemEntity) {
        try {
            beginTx();
            em.persist(itemEntity);
            commitTx();
        }
        catch (Exception e) {
            rollbackTx();
        }

        if (itemEntity.getId() == null) {
            throw new RuntimeException("Entity was not persisted");
        }
        return itemEntity;
    }

    public Item putItem(Item item, Integer id) {
        ItemEntity itemEntity = em.find(ItemEntity.class, id);
        if (itemEntity == null) {
            return null;
        }

        ItemEntity updatedItemEntity = ItemConverter.toEntity(item);
        try {
            beginTx();
            updatedItemEntity.setId(itemEntity.getId());
            updatedItemEntity = em.merge(updatedItemEntity);
            commitTx();
        } catch (Exception e) {
            rollbackTx();
            log.warning(e.getMessage());
            return null;
        }
        return ItemConverter.toDto(updatedItemEntity);
    }

    public Item patchItem(Item item, Integer id) {
        ItemEntity itemEntity = em.find(ItemEntity.class, id);
        if (itemEntity == null) {
            return null;
        }

        ItemEntity updatedItemEntity = ItemConverter.toEntity(item);
        try {
            beginTx();
            if (updatedItemEntity.getCategory() == null) {
                updatedItemEntity.setCategory(itemEntity.getCategory());
            }
            if (updatedItemEntity.getDescription() == null) {
                updatedItemEntity.setDescription(itemEntity.getDescription());
            }
            if (updatedItemEntity.getTitle() == null) {
                updatedItemEntity.setTitle(itemEntity.getTitle());
            }
            if (updatedItemEntity.getUserId() == null) {
                updatedItemEntity.setUserId(itemEntity.getUserId());
            }
            if (updatedItemEntity.getScore() == null) {
                updatedItemEntity.setScore(itemEntity.getScore());
            }
            if (updatedItemEntity.getStatus() == null) {
                updatedItemEntity.setStatus(itemEntity.getStatus());
            }
            if (updatedItemEntity.getUri() == null) {
                updatedItemEntity.setUri(itemEntity.getUri());
            }
            if (updatedItemEntity.getTimestamp() == null) {
                updatedItemEntity.setTimestamp(itemEntity.getTimestamp());
            }
            updatedItemEntity.setId(itemEntity.getId());
            updatedItemEntity = em.merge(updatedItemEntity);
            commitTx();
        } catch (Exception e) {
            rollbackTx();
            log.warning(e.getMessage());
            return null;
        }
        return ItemConverter.toDto(updatedItemEntity);
    }

    public boolean deleteItem(Integer id) {
        ItemEntity itemEntity = em.find(ItemEntity.class, id);
        if (itemEntity == null) {
            return false;
        }
        try {
            beginTx();
            em.remove(itemEntity);
            commitTx();
        } catch (Exception e) {
            rollbackTx();
            log.warning(e.getMessage());
            return false;
        }
        return true;
    }

    private void beginTx() {
        if (!em.getTransaction().isActive()) {
            em.getTransaction().begin();
        }
    }

    private void commitTx() {
        if (em.getTransaction().isActive()) {
            em.getTransaction().commit();
        }
    }

    private void rollbackTx() {
        if (em.getTransaction().isActive()) {
            em.getTransaction().rollback();
        }
    }
}