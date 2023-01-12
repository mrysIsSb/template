package top.mrys.custom.customizers;

import io.swagger.v3.oas.models.tags.Tag;

import java.util.List;

/**
 * @author mrys
 */
public interface ApiTagsCustomizer {

  void customize(List<Tag> tags);
}
