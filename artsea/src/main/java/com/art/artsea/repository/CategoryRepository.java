package com.art.artsea.repository;
import com.art.artsea.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    @Query("SELECT COUNT(c) FROM Category c")
    long countCategories();
}