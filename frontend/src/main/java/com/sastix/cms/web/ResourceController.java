package com.sastix.cms.web;

import com.sastix.cms.client.impl.CmsClient;
import com.sastix.cms.common.cache.CacheDTO;
import com.sastix.cms.common.cache.QueryCacheDTO;
import com.sastix.cms.common.content.CreateResourceDTO;
import com.sastix.cms.common.content.ResourceDTO;
import com.sastix.cms.common.content.ResourceQueryDTO;
import com.sastix.cms.common.content.exceptions.ContentValidationException;
import com.sastix.cms.common.content.exceptions.ResourceAccessError;
import com.sastix.cms.common.content.exceptions.ResourceNotFound;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Controller
public class ResourceController {

    public static final String TENANT_ID = "zaq12345";

    @Autowired
    private CmsClient cmsClient;

    @RequestMapping("resource/{uid}")
    public String showResource(@PathVariable String uid, Model model) {
        ResourceQueryDTO resourceQueryDTO = new ResourceQueryDTO();
        resourceQueryDTO.setQueryUID(uid);
        ResourceDTO resourceDTO = cmsClient.queryResource(resourceQueryDTO);
        model.addAttribute("resource", resourceDTO);
        return "resourceshow";
    }

    @RequestMapping(value = "/resources", method = RequestMethod.GET)
    public String listResources(Model model) {

        ResourceQueryDTO resourceQueryDTO = new ResourceQueryDTO();
        resourceQueryDTO.setQueryResourceAuthor("Andreas Daskalopoulos");
        resourceQueryDTO.setQueryUID("");
        ResourceDTO resourceDTO = cmsClient.queryResource(resourceQueryDTO);

        List<ResourceDTO> resourceDTOS = resourceDTO.getResourcesList();
        model.addAttribute("resources", resourceDTO.getResourcesList());
        System.out.println("Returning resources:");
        return "resources";
    }

    @RequestMapping("resources/new")
    public String newResource(Model model) {
        model.addAttribute("resource", new CreateResourceDTO());
        return "resourceform";
    }

    @RequestMapping(value = "resource", method = RequestMethod.POST)
    public String saveResource(@RequestParam("file") MultipartFile file, @RequestParam("author") String author) {
        CreateResourceDTO createResourceDTO = new CreateResourceDTO();
        createResourceDTO.setResourceAuthor(author);
        createResourceDTO.setResourceMediaType(file.getContentType());
        createResourceDTO.setResourceName(file.getName());
        createResourceDTO.setResourceTenantId(TENANT_ID);
        try {
            createResourceDTO.setResourceBinary(file.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
        ResourceDTO resourceDTO = cmsClient.createResource(createResourceDTO);
        return "redirect:/resource/" + resourceDTO.getResourceUID();
    }
}
