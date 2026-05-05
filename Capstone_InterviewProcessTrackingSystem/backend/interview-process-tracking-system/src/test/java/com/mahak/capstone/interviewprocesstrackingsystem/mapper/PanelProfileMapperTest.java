package com.mahak.capstone.interviewprocesstrackingsystem.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import com.mahak.capstone.interviewprocesstrackingsystem.dto.PanelProfileRequestDTO;
import com.mahak.capstone.interviewprocesstrackingsystem.dto.PanelProfileResponseDTO;
import com.mahak.capstone.interviewprocesstrackingsystem.entity.PanelProfile;
import com.mahak.capstone.interviewprocesstrackingsystem.entity.User;

public class PanelProfileMapperTest {

    private final PanelProfileMapper mapper = new PanelProfileMapper();

    @Test
    void testToEntity() {
        PanelProfileRequestDTO dto = new PanelProfileRequestDTO();
        dto.setOrganization("NT");
        
        User user = new User();
        user.setId(1L);
        
        PanelProfile entity = mapper.toEntity(dto, user);
        
        assertNotNull(entity);
        assertEquals("NT", entity.getOrganization());
        assertEquals(user, entity.getUser());
    }

    @Test
    void testToDTO() {
        PanelProfile entity = new PanelProfile();
        org.springframework.test.util.ReflectionTestUtils.setField(entity, "id", 1L);
        entity.setOrganization("NT");
        
        User user = new User();
        user.setId(1L);
        user.setFullName("Panelist");
        entity.setUser(user);
        
        PanelProfileResponseDTO dto = mapper.toResponseDTO(entity);
        
        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals("Panelist", dto.getName());
    }
}
