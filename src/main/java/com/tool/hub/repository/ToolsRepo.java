/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tool.hub.repository;

import com.tool.hub.entity.Tools;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author Deepak
 */
public interface ToolsRepo extends JpaRepository<Tools,  Long> {
    
}
