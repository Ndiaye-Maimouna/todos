package sn.ept.git.seminaire.cicd.services;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;
import sn.ept.git.seminaire.cicd.data.TagTestData;
import sn.ept.git.seminaire.cicd.entities.Tag;
import sn.ept.git.seminaire.cicd.exceptions.ItemExistsException;
import sn.ept.git.seminaire.cicd.exceptions.ItemNotFoundException;
import sn.ept.git.seminaire.cicd.mappers.TagMapper;
import sn.ept.git.seminaire.cicd.models.TagDTO;
import sn.ept.git.seminaire.cicd.repositories.TagRepository;

import java.util.*;
/////////////////////
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Slf4j
@ExtendWith(MockitoExtension.class)
class TagServiceTest {

    @Mock
    TagRepository tagRepository;

    @InjectMocks
    TagService service;

    private static final TagMapper mapper = Mappers.getMapper(TagMapper.class);

    Tag tag;
    TagDTO dto;
    String newName = "updatedTagName";
    int page = 0;
    int size = 10;

    @BeforeEach
    void beforeEach() {
        log.info("before each");
        ReflectionTestUtils.setField(service, "mapper", mapper);
        tag = TagTestData.defaultTag();
        dto = mapper.toDTO(tag);
    }

    @Test
    void save_shouldSaveTag() {
        Mockito.when(tagRepository.findByName(dto.getName()))
                .thenReturn(Optional.empty());

        Mockito.when(tagRepository.saveAndFlush(Mockito.any()))
                .thenReturn(tag);

        TagDTO saved = service.save(dto);

        assertThat(saved)
                .isNotNull()
                .hasNoNullFieldsOrProperties();
    }

    @Test
    void delete_shouldDeleteTag() {
        Mockito.when(tagRepository.findById(Mockito.anyString()))
                .thenReturn(Optional.of(tag));

        Mockito.doNothing().when(tagRepository).deleteById(Mockito.anyString());

        assertDoesNotThrow(() -> service.delete(dto.getId()));
    }

    @Test
    void delete_withBadId_shouldThrowException() {
        Mockito.when(tagRepository.findById(Mockito.anyString()))
                .thenReturn(Optional.empty());

        assertThrows(ItemNotFoundException.class,
                () -> service.delete(dto.getId()));
    }

    @Test
    void findById_shouldReturnResult() {
        Mockito.when(tagRepository.findById(Mockito.anyString()))
                .thenReturn(Optional.of(tag));

        TagDTO found = service.findById(tag.getId());

        assertThat(found)
                .isNotNull()
                .hasFieldOrPropertyWithValue("id", tag.getId())
                .hasFieldOrPropertyWithValue("name", tag.getName());
    }

    @Test
    void findById_withBadId_shouldThrowException() {
        Mockito.when(tagRepository.findById(Mockito.anyString()))
                .thenReturn(Optional.empty());

        assertThrows(ItemNotFoundException.class,
                () -> service.findById(UUID.randomUUID().toString()));
    }

    @Test
    void findAllPageable_shouldReturnResult() {
        Pageable pageable = PageRequest.of(page, size);

        Mockito.when(tagRepository.findAll(Mockito.any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(tag), pageable, 1));

        Page<TagDTO> result = service.findAll(pageable);

        assertThat(result)
                .isNotNull()
                .isNotEmpty()
                .hasSize(1);
    }

    @Test
    void update_shouldSucceed() {
        dto.setName(newName);

        Mockito.when(tagRepository.findById(Mockito.anyString()))
                .thenReturn(Optional.of(tag));

        Mockito.when(tagRepository.findByNameWithIdNotEquals(Mockito.eq(newName), Mockito.anyString()))
                .thenReturn(Optional.empty());

        Mockito.when(tagRepository.saveAndFlush(Mockito.any()))
                .thenReturn(tag);

        TagDTO updated = service.update(dto.getId(), dto);

        assertThat(updated)
                .isNotNull()
                .hasFieldOrPropertyWithValue("name", newName);
    }

    @Test
    void update_withBadId_shouldThrowException() {
        Mockito.when(tagRepository.findById(Mockito.anyString()))
                .thenReturn(Optional.empty());

        assertThrows(ItemNotFoundException.class,
                () -> service.update(UUID.randomUUID().toString(), dto));
    }

    @Test
    void deleteAll_shouldDeleteAllTags() {
        Mockito.doNothing().when(tagRepository).deleteAll();

        assertDoesNotThrow(() -> service.deleteAll());
    }

    @Test
    void update_withExistingName_shouldThrowException() {
        dto.setName(newName);

        Mockito.when(tagRepository.findById(Mockito.anyString()))
                .thenReturn(Optional.of(tag));

        Mockito.when(tagRepository.findByNameWithIdNotEquals(Mockito.eq(newName), Mockito.anyString()))
                .thenReturn(Optional.of(new Tag()));

        assertThrows(ItemExistsException.class,
                () -> service.update(dto.getId(), dto));
    }
}
