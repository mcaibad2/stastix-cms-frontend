package com.sastix.cms.web;

import com.sastix.cms.client.impl.CmsClient;
import com.sastix.cms.common.cache.QueryCacheDTO;
import com.sastix.cms.common.cache.RemoveCacheDTO;
import com.sastix.cms.common.content.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Controller
public class ResourceController {

    public static final String TENANT_ID = "zaq12345";

    @Autowired
    private CmsClient cmsClient;

    @RequestMapping(value = "/resources", method = RequestMethod.GET)
    public String listResources(Model model) {
        ResourceQueryDTO resourceQueryDTO = new ResourceQueryDTO();
        model.addAttribute("resources", cmsClient.listAllResources(resourceQueryDTO));
        System.out.println("Returning resources:");
        return "resources";
    }

    @RequestMapping(value = "resource/{uid}", method = RequestMethod.GET)
    public String showResource(@PathVariable String uid, Model model) {
        ResourceQueryDTO resourceQueryDTO = new ResourceQueryDTO();
        resourceQueryDTO.setQueryUID(uid);
        model.addAttribute("resource", cmsClient.queryResource(resourceQueryDTO));
        return "resourceshow";
    }

    @RequestMapping("resources/new")
    public String newResource(Model model) {
        model.addAttribute("resource", new CreateResourceDTO());
        return "resourceform";
    }

    @RequestMapping("resource/delete/{uid}")
    public String deleteResource(@PathVariable String uid) {
        ResourceQueryDTO resourceQueryDTO = new ResourceQueryDTO();
        resourceQueryDTO.setQueryUID(uid);
        ResourceDTO resourceDTO = cmsClient.queryResource(resourceQueryDTO);

        LockedResourceDTO lockedResourceDTO = cmsClient.lockResource(resourceDTO);

        ResourceDTO resourceDTO1 = cmsClient.deleteResource(lockedResourceDTO);
        return "redirect:/resources";
    }

    @RequestMapping("resource/edit/{uid}")
    public String edit(@PathVariable String uid, Model model) {
        ResourceQueryDTO resourceQueryDTO = new ResourceQueryDTO();
        resourceQueryDTO.setQueryUID(uid);
        ResourceDTO resourceDTO = cmsClient.queryResource(resourceQueryDTO);
        model.addAttribute("resource", resourceDTO);
        return "resourceedit";
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

    @RequestMapping(value = "editResource", method = RequestMethod.POST)
    public String editResource(ResourceDTO resourceDTO) {
        UpdateResourceDTO updateResourceDTO = new UpdateResourceDTO();
        updateResourceDTO.setResourceUID(resourceDTO.getResourceUID());
        updateResourceDTO.setAuthor(resourceDTO.getAuthor());
//        updateResourceDTO.setResourceName();
//        updateResourceDTO.setLockExpirationDate();
//        updateResourceDTO.setLockID();
        LockedResourceDTO lockedResourceDTO = cmsClient.updateResource(updateResourceDTO);

        return "redirect:/resource/" + lockedResourceDTO.getResourceUID();
    }
}
