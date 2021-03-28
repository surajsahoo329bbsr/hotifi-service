package com.api.hotifi.configuration;

import com.google.common.base.Optional;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.OperationBuilderPlugin;
import springfox.documentation.spi.service.contexts.OperationContext;
import springfox.documentation.spring.web.DescriptionResolver;
import springfox.documentation.swagger.common.SwaggerPluginSupport;

import java.util.logging.Level;
import java.util.logging.Logger;

@Component
@Order(SwaggerPluginSupport.SWAGGER_PLUGIN_ORDER + 1)
public class OperationNotesResourcesReader implements OperationBuilderPlugin {

    private static final Logger LOG = Logger.getLogger(OperationNotesResourcesReader.class.getName());

    private final DescriptionResolver descriptions;

    @Autowired
    public OperationNotesResourcesReader(DescriptionResolver descriptions) {
        this.descriptions = descriptions;
    }

    @Override
    public void apply(OperationContext context) {
        try {
            StringBuilder sb = new StringBuilder();

            // Check authorization
            Optional<PreAuthorize> preAuthorizeAnnotation = context.findAnnotation(PreAuthorize.class);
            sb.append("<b>Access Privileges & Rules</b>: ");
            if (preAuthorizeAnnotation.isPresent()) {
                sb.append("<em>").append(preAuthorizeAnnotation.get().value()).append("</em>");
            } else {
                sb.append("<em>NOT_FOUND</em>");
            }

            // Check notes
            Optional<ApiOperation> annotation = context.findAnnotation(ApiOperation.class);
            if (annotation.isPresent() && StringUtils.hasText(annotation.get().notes())) {
                sb.append("<br /><br />");
                sb.append(annotation.get().notes());
            }

            // Add the note text to the Swagger UI
            context.operationBuilder().notes(descriptions.resolve(sb.toString()));
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Error when creating swagger documentation for security roles: ", e);
        }
    }

    @Override
    public boolean supports(DocumentationType delimiter) {
        return SwaggerPluginSupport.pluginDoesApply(delimiter);
    }
}
