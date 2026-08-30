package com.example.order_service.infrastructure.adapters.out.persistence.repository;

import com.example.order_service.infrastructure.adapters.out.persistence.entity.OrderEntity;
import org.springframework.data.cassandra.repository.AllowFiltering;
import org.springframework.data.cassandra.repository.CassandraRepository;

import java.util.List;
import java.util.UUID;

public interface SpringDataOrderRepository  extends CassandraRepository<OrderEntity, UUID> {
    @AllowFiltering
    List<OrderEntity> findByUserId(UUID userId);
}
