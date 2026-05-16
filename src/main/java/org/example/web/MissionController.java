package org.example.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.entity.MissionEntity;
import org.example.service.MissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/missions")
@Tag(name = "Missions", description = "Операции с архивом миссий колледжа")
public class MissionController {

    private final MissionService missionService;

    @Autowired
    public MissionController(MissionService missionService) {
        this.missionService = missionService;
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Загрузить миссию",
               description = "Принимает файл миссии, парсит его и сохраняет в архив. " +
                             "Поддерживаемые форматы: json, xml, yaml/yml, txt, pipe (разделитель |)")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Миссия успешно сохранена в архиве"),
        @ApiResponse(responseCode = "409", description = "Миссия с таким ID уже существует"),
        @ApiResponse(responseCode = "422", description = "Не удалось распарсить файл или данные невалидны"),
        @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    public ResponseEntity<?> upload(
            @RequestParam("file")
            @Parameter(description = "Файл миссии", required = true)
            MultipartFile file) {
        try {
            MissionEntity saved = missionService.upload(file);
            return ResponseEntity.status(HttpStatus.CREATED).body(
                    Map.of("id", saved.getId(),
                           "missionId", saved.getMissionId(),
                           "message", "Миссия сохранена в архиве")
            );
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping
    @Operation(summary = "Архив миссий",
               description = "Возвращает список всех сохранённых миссий, отсортированных по дате добавления")
    @ApiResponse(responseCode = "200", description = "Список миссий")
    public List<MissionEntity> listAll() {
        return missionService.listAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Данные миссии",
               description = "Возвращает полную информацию о конкретной миссии по её id в базе данных")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Данные миссии"),
        @ApiResponse(responseCode = "404", description = "Миссия не найдена")
    })
    public ResponseEntity<?> getById(
            @PathVariable @Parameter(description = "ID миссии в БД") Long id) {
        try {
            return ResponseEntity.ok(missionService.getById(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping(value = "/{id}/report", produces = MediaType.TEXT_PLAIN_VALUE)
    @Operation(summary = "Отчёт по миссии",
               description = "Извлекает данные миссии из архива и генерирует подробный текстовый отчёт")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Текст отчёта"),
        @ApiResponse(responseCode = "404", description = "Миссия не найдена")
    })
    public ResponseEntity<String> report(
            @PathVariable @Parameter(description = "ID миссии в БД") Long id) {
        try {
            return ResponseEntity.ok(missionService.generateReport(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить миссию",
               description = "Удаляет миссию и все связанные данные (маги, техники) из архива")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Миссия удалена"),
        @ApiResponse(responseCode = "404", description = "Миссия не найдена")
    })
    public ResponseEntity<?> delete(
            @PathVariable @Parameter(description = "ID миссии в БД") Long id) {
        try {
            missionService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }
}
