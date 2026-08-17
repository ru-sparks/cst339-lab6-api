package com.sparkco.lab2_api;

import java.util.Set;

import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springdoc.core.utils.SpringDocUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Pageable;

import io.swagger.v3.oas.models.parameters.Parameter;

/**
 * Document {@code page}, {@code size}, and {@code sort} as optional query parameters.
 * Ignore the raw {@link Pageable} type so Swagger does not show a required object.
 */
@Configuration
public class OpenApiConfig {

    static {
        SpringDocUtils.getConfig().addRequestWrapperToIgnore(Pageable.class);
    }

    private static final Set<String> PAGEABLE_PARAM_NAMES = Set.of("page", "size", "sort", "pageable");

    private static final String SORT_EXAMPLE = "title";

    @Bean
    public OpenApiCustomizer pageableQueryDefaults() {
        return openApi -> {
            if (openApi.getPaths() == null) {
                return;
            }
            openApi.getPaths().values().forEach(pathItem -> pathItem.readOperations()
                    .forEach(operation -> {
                        if (operation.getParameters() == null) {
                            return;
                        }
                        operation.getParameters().stream()
                                .filter(parameter -> PAGEABLE_PARAM_NAMES.contains(parameter.getName()))
                                .forEach(OpenApiConfig::documentPageableQuery);
                    }));
        };
    }

    private static void documentPageableQuery(Parameter parameter) {
        parameter.setRequired(false);
        switch (parameter.getName()) {
            case "page" -> {
                parameter.setDescription("0-based page index. Omitted: 0.");
                setNumericDefault(parameter, 0);
            }
            case "size" -> {
                parameter.setDescription("Page size. Omitted: 20 (@PageableDefault).");
                setNumericDefault(parameter, 20);
            }
            case "sort" -> documentAlbumSort(parameter);
            case "pageable" -> {
                parameter.setDescription(
                        "Optional. Omit the query string for page 0, size 20, unsorted. "
                                + "Swagger may mark this required; the server does not.");
                if (parameter.getSchema() != null) {
                    parameter.getSchema().setRequired(null);
                }
            }
            default -> {
                // already filtered to pageable names
            }
        }
    }

    private static void documentAlbumSort(Parameter parameter) {
        parameter.setDescription(
                "Album property to sort by, optionally with direction. Examples: title, title,desc, albumId. "
                        + "Omit or use an empty array for database order. Do not send the type placeholder \"string\".");
        parameter.setExample(SORT_EXAMPLE);
        if (parameter.getSchema() != null) {
            parameter.getSchema().setExample(SORT_EXAMPLE);
            parameter.getSchema().setDefault(null);
            if (parameter.getSchema().getItems() != null) {
                parameter.getSchema().getItems().setExample(SORT_EXAMPLE);
                parameter.getSchema().getItems().setDefault(null);
            }
        }
    }

    private static void setNumericDefault(Parameter parameter, int value) {
        parameter.setExample(value);
        if (parameter.getSchema() != null) {
            parameter.getSchema().setDefault(value);
            parameter.getSchema().setExample(value);
        }
    }
}
