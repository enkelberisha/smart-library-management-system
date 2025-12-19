package com.smartLib.managementSystem.model.dto;

public record BookRow(
        int id,
        String title,
        String author,
        String genre,
        String status
) {}