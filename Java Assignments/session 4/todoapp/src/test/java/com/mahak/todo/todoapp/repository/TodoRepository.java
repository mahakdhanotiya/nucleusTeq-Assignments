package com.mahak.todo.todoapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mahak.todo.todoapp.entity.Todo;

public interface TodoRepository extends JpaRepository<Todo, Long> {
}
