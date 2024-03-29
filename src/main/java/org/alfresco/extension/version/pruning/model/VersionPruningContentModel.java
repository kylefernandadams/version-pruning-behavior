package org.alfresco.extension.version.pruning.model;

import org.alfresco.service.namespace.QName;


/**
 * Created by kadams on 4/14/14.
 */
public class VersionPruningContentModel {

    public static final String VERSION_PRUNING_URI = "http://www.alfresco.org/model/extension/version-pruning/1.0";
    public static final String VERSION_PRUNING_PREFIX = "prune";
    public static final QName VERSION_PRUNING_MODEL = QName.createQName(VERSION_PRUNING_URI, "versionPruningModel");
    public static final QName ASPECT_VERSION_PRUNABLE = QName.createQName(VERSION_PRUNING_URI, "versionPrunable");
    public static final QName PROP_EVALUATION_TYPE = QName.createQName(VERSION_PRUNING_URI, "evaluationType");
    public static final QName PROP_VERSION_TYPE = QName.createQName(VERSION_PRUNING_URI, "versionType");
    public static final QName PROP_THRESHOLD_COUNT = QName.createQName(VERSION_PRUNING_URI, "thresholdMax");
    public static final QName PROP_THRESHOLD_SIZE = QName.createQName(VERSION_PRUNING_URI, "thresholdSizeMax");
    public static final QName PROP_KEEP_ROOT_VERSION = QName.createQName(VERSION_PRUNING_URI, "keepRootVersion");

}
