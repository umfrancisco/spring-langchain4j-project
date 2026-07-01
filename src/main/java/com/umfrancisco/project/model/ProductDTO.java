package com.umfrancisco.project.model;

import java.math.BigDecimal;

public record ProductDTO(Long id, String name, BigDecimal price, Integer stock) {
	
	@Override
	public String toString() {
		return "{id=%d, name=%s, price=%.2f, stock=%d}".formatted(id, name, price, stock);
	}
}
