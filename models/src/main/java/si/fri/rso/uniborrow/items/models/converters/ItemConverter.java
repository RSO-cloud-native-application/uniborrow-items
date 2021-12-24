package si.fri.rso.uniborrow.items.models.converters;

import si.fri.rso.uniborrow.items.lib.Item;
import si.fri.rso.uniborrow.items.models.entities.ItemEntity;

public class ItemConverter {

    public static Item toDto(ItemEntity entity) {

        Item dto = new Item();
        dto.setItemId(entity.getId());
        dto.setTimestamp(entity.getTimestamp());
        dto.setDescription(entity.getDescription());
        dto.setTitle(entity.getTitle());
        dto.setScore(entity.getScore());
        dto.setUserId(entity.getUserId());
        dto.setUri(entity.getUri());
        dto.setStatus(entity.getStatus());
        dto.setCategory(entity.getCategory());

        return dto;

    }

    public static ItemEntity toEntity(Item dto) {

        ItemEntity entity = new ItemEntity();
        entity.setTimestamp(dto.getTimestamp());
        entity.setDescription(dto.getDescription());
        entity.setTitle(dto.getTitle());
        entity.setScore(dto.getScore());
        entity.setUserId(dto.getUserId());
        entity.setUri(dto.getUri());
        entity.setStatus(dto.getStatus());
        entity.setCategory(dto.getCategory());

        return entity;

    }

}
