package com.monk.couponsmgmt.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

@Component
public class ObjectMapperUtil {

    private static ObjectMapper objectMapper;

    public ObjectMapperUtil(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    // Convert DTO to Entity
    public static <D, E> E dtoToEntity(D dto, Class<E> entityClass) throws Exception {
        return objectMapper.convertValue(dto, entityClass);
    }

    // Convert Entity to DTO
    public static <E, D> D entityToDto(E entity, Class<D> dtoClass) throws Exception {
        return objectMapper.convertValue(entity, dtoClass);
    }
}
