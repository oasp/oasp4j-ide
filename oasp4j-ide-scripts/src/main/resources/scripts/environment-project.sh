. $OASP_PROJECT_HOME/variables.sh
if [ -f $OASP_PROJECT_HOME/variables-customized.sh ]; then
    . $OASP_PROJECT_HOME/variables-customized.sh
fi

if [ -z $OASP_WORKSPACE ]; then
    export OASP_WORKSPACE=$OASP_MAIN_BRANCH
fi
export OASP_WORKSPACE_REL_PATH=workspaces/$OASP_WORKSPACE
export OASP_WORKSPACE_PATH=$OASP_WORKSPACES_PATH/$OASP_WORKSPACE

export OASP_WORKSPACE_PLUGINS_PATH=$OASP_WORKSPACE_PATH/.metadata/.plugins
export OASP_SETTINGS_PATH=$OASP_WORKSPACE_PATH/$OASP_SETTINGS_REL_PATH
export OASP_ECLIPSE_TEMPLATES_PATH=$OASP_WORKSPACE_REL_PATH/$OASP_SETTINGS_REL_PATH/$OASP_ECLIPSE_TEMPLATES_REL_PATH

if [ -f $OASP_SETTINGS_PATH/ide-properties.sh ]; then
    . $OASP_SETTINGS_PATH/ide-properties.sh
fi

export OASP4J_IDE_VERSION=1.3.1
export OASP4J_ECLIPSE_CONFIGURATOR=oasp4j-ide-eclipse-configurator-1.3.1.jar

###
# JAVA
# TODO: Port this
###

###
# MAVEN
###
export M2_HOME=$OASP_SOFTWARE_PATH/maven
export M2_CONF=$OASP_CONF_PATH/.m2/settings.xml
export MAVEN_OPTS="-Xmx512m -Duser.home=$OASP_CONF_PATH"
export MAVEN_HOME=$M2_HOME
export PATH=$M2_HOME/bin:$PATH

###
# Eclipse
# TODO: Port this
###

###
# NodeJS
# TODO: Port this
###

