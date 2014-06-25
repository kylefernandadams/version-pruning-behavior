package org.alfresco.extension.version.pruning.behaviour;

import org.alfresco.extension.version.pruning.model.VersionPruningContentModel;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.policy.Behaviour;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.repo.version.VersionServicePolicies;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.version.Version;
import org.alfresco.service.cmr.version.VersionHistory;
import org.alfresco.service.cmr.version.VersionService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.common.collect.Iterators;

/**
 * Created by kadams on 4/14/14.
 */
public class VersionPruningBehaviour implements VersionServicePolicies.AfterCreateVersionPolicy {
    private static final Log logger = LogFactory.getLog(VersionPruningBehaviour.class);

    private ServiceRegistry serviceRegistry;
    private PolicyComponent policyComponent;
    private NodeService nodeService;
    private VersionService versionService;

    private int versionMax;
    private boolean keepRoot;

    public void init(){
        this.policyComponent.bindClassBehaviour(
                VersionServicePolicies.AfterCreateVersionPolicy.QNAME,
                ContentModel.TYPE_CONTENT,
                new JavaBehaviour(this, "afterCreateVersion", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));

        this.nodeService = this.serviceRegistry.getNodeService();
        this.versionService = this.serviceRegistry.getVersionService();

    }

    @Override
    public void afterCreateVersion(NodeRef nodeRef, Version version) {
        logger.debug("New version created: " + version.getVersionLabel() + " type: " + version.getVersionType() + " description: " + version.getDescription());

        try{
        	if(this.nodeService.hasAspect(nodeRef, VersionPruningContentModel.ASPECT_VERSION_PRUNABLE)){
            	if(hasVersionHistory(nodeRef)){
                    VersionHistory versionHistory = this.versionService.getVersionHistory(nodeRef);

                    while(versionHistory.getAllVersions().size() > this.versionMax){
                    	deleteVersion(nodeRef, versionHistory);
                    	versionHistory = this.versionService.getVersionHistory(nodeRef);
                    }                    
                }
                else{
                    logger.debug("No version history found!");
                }
            }
            else{
            	logger.debug("Node does not contain the aspect: " + VersionPruningContentModel.ASPECT_VERSION_PRUNABLE);
            }
        }
        catch(Exception e){
        	logger.error("Failed to execute behaviour:",  e);
        }
    }

	private void deleteVersion(NodeRef nodeRef, VersionHistory versionHistory){

        try{
        	if(this.keepRoot){
        		Version secondToLastVersion = Iterators.get(versionHistory.getAllVersions().iterator(), 
						(versionHistory.getAllVersions().size()-2));
        		logger.debug("Preparing to delete version: " + secondToLastVersion.getVersionLabel() + " with description: " + secondToLastVersion.getDescription());
        		this.versionService.deleteVersion(nodeRef, secondToLastVersion);
        	}
        	else{
        		Version rootVersion = versionHistory.getRootVersion();
        		logger.debug("Preparing to delete root version: " + rootVersion.getVersionLabel() + " with description: " + rootVersion.getDescription());
        		this.versionService.deleteVersion(nodeRef, versionHistory.getRootVersion());
        	}
        }
        catch (Exception e){
        	logger.error("Failed to delete version: ", e);
        }
    }

    public boolean hasVersionHistory(NodeRef nodeRef){
        VersionHistory versionHistory = this.versionService.getVersionHistory(nodeRef);

        if(versionHistory == null){
            return false;
        }
        else{
            return true;
        }
    }

    public void setServiceRegistry(ServiceRegistry serviceRegistry) {
        this.serviceRegistry = serviceRegistry;
    }

    public void setPolicyComponent(PolicyComponent policyComponent) {
        this.policyComponent = policyComponent;
    }

    public void setVersionMax(int versionMax) {
        this.versionMax = versionMax;
    }

    public void setKeepRoot(boolean keepRoot) {
        this.keepRoot = keepRoot;
    }
}
