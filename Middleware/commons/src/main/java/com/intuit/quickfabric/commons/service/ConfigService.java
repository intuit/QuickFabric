package com.intuit.quickfabric.commons.service;

import com.intuit.quickfabric.commons.helper.ConfigHelper;
import com.intuit.quickfabric.commons.utils.CommonUtils;
import com.intuit.quickfabric.commons.vo.ConfigDefinitionVO;
import com.intuit.quickfabric.commons.vo.ConfigVO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;


@RestController
@RequestMapping("/configurations")
public class ConfigService {

    private static final Logger logger = LogManager.getLogger(ConfigService.class);

    @Autowired
    ConfigHelper configHelper;

    /** Get the definitions for all configurations in QuickFabric
     * @return the configuration definitions
     */
    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @GetMapping(value = "/definitions")
    @PreAuthorize("hasAnyAuthority('superadmin')")
    public ResponseEntity getAllConfigurationDefinitions() {
        List<ConfigDefinitionVO> configDefinitions;
        try {
            logger.info("getting configuration definitions");
            configDefinitions = configHelper.getAllConfigurationDefinitions();
        } catch (Exception e) {
            logger.error("Exception occurred during config/definitions: " + e.getMessage(), e);
            return CommonUtils.createErrorResponse(e);
        }
        return ResponseEntity.ok(configDefinitions);
    }

    /** Add a value for the given configuration. Used for both application and account level 
     * configurations
     * @param configVO the config name, the account (if applicable), and the value to set it to
     * @return
     */
    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PostMapping()
    @PreAuthorize("hasAnyAuthority('superadmin')")
    public ResponseEntity addConfig(@RequestBody @Valid ConfigVO configVO) {
        try {
            configHelper.addConfig(configVO);
        } catch (Exception e) {
            logger.error("configuration service, addConfig Exception: " + e.getMessage(), e);
            return CommonUtils.createErrorResponse(e);
        }

        return ResponseEntity.ok("config successfully added");
    }

    /** Get the value for the given configuration, either application or account level
     * @param configName the name of the config to retrieve
     * @param accountId the AWS account for the config (if applicable)
     * @return
     */
    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PreAuthorize("hasAnyAuthority('superadmin', 'read')")
    @GetMapping(value = {"/{configName}", "/{configName}/{accountId}"})
    public ResponseEntity<ConfigVO> getConfig(@PathVariable(value = "configName") String configName,
                                              @PathVariable(value = "accountId", required = false) String accountId) {
        ResponseEntity<ConfigVO> result;
        logger.info("getting config for name:" + configName + " accountId:" + accountId);

        try {
            ConfigVO configDetails = configHelper.getConfig(configName, accountId);
            result = ResponseEntity.ok(configDetails);

        } catch (Exception e) {
            logger.error("Exception occurred while getting config.", e);
            result = CommonUtils.createErrorResponse(e);
        }
        return result;
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PreAuthorize("hasAnyAuthority('superadmin')")
    @GetMapping(value = {"/list", "/list/{accountId}"})
    public ResponseEntity<List<ConfigVO>> getConfigurations(@PathVariable(value = "accountId", required = false) String accountId) {
        logger.info("getting all configurations. accountId:" + accountId);
        ResponseEntity result;
        try {
            List<ConfigVO> configDetails = configHelper.getAllConfigurations(accountId, true);
            result = ResponseEntity.ok(configDetails);

        } catch (Exception e) {
            logger.error("Exception occurred while getting config.", e);
            result = CommonUtils.createErrorResponse(e);
        }
        return result;
    }

    /**
     * Return the decrypted value of the input config, application or account level
     * 
     * @param configName the name of the config to decrpyt
     * @param accountId the AWS account for the config (if applicable)
     * @return
     */
    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PreAuthorize("hasAnyAuthority('superadmin', 'read')")
    @GetMapping(value = {"/decrypt/{configName}", "/decrypt/{configName}/{accountId}"})
    public ResponseEntity decryptConfig(@PathVariable(value = "configName") String configName,
                                        @PathVariable(value = "accountId", required = false) String accountId) {
        ResponseEntity result;
        logger.info("decrypting for config:" + configName + " accountId:" + accountId);

        try {
            ConfigVO config = configHelper.decryptConfig(configName, accountId);
            result = ResponseEntity.ok(config);
        } catch (Exception e) {
            logger.error("Exception occurred while decrypting.", e);
            result = CommonUtils.createErrorResponse(e);
        }
        return result;
    }
}
