#!/bin/bash
set -e

pushd $OASP_PROJECT_HOME > /dev/null

if [ -n $1 ]; then
    OASP_WORKSPACE=$1
fi

. scripts/environment-project.sh

if [ ! -d $OASP_SOFTWARE_PATH ]; then
    echo "Could not find folder $OASP_SOFTWARE_PATH"
    echo "If you want to change its name see the variables.bat"
    echo "Execution aborted"
    exit 1
fi
if [ ! -d $OASP_WORKSPACES_PATH ]; then
    echo "Could not find folder $OASP_WORKSPACES_PATH"
    echo "If you want to change its name see the variables.bat"
    echo "Execution aborted"
    exit 1
fi
if [ ! -d $OASP_WORKSPACE_PATH ]; then
    echo "Could not find workspace $OASP_WORKSPACE_PATH"
    echo "Execution aborted"
    exit 1
fi
if [ ! -d $OASP_SETTINGS_PATH ]; then
    echo "Could not find folder $OASP_SETTINGS_PATH"
    echo "If you want to change its name see the variables.bat"
    echo "Execution aborted"
    exit 1
fi


if [ ! -e $OASP_CONF_PATH/.m2 ]; then
    mkdir $OASP_CONF_PATH/.m2
fi
if [ ! -e $OASP_CONF_PATH/.m2/settings.xml ]; then
    cp $OASP_SETTINGS_PATH/maven/settings.xml $OASP_CONF_PATH/.m2/settings.xml
    echo "Copied $OASP_SETTINGS_PATH/maven/settings.xml to $OASP_CONF_PATH/.m2/settings.xml"
fi

# TODO: Port subversion config


if [ ! -d $OASP_ECLIPSE_TEMPLATES_PATH ]; then
    echo "Could not find folder $OASP_ECLIPSE_TEMPLATES_PATH"
    echo "Execution aborted"
    exit 1
fi
if [ ! -e $OASP_ECLIPSE_REPLACEMENT_PATTERNS_PATH ]; then
    echo "Could not find file $OASP_ECLIPSE_REPLACEMENT_PATTERNS_PATH"
    echo "Execution aborted"
    exit 1
fi

# copys/merges the *.prefs form $OASP_SETTINGS_PATH/eclipse/workspace\... to specified Eclipse workspace
# In order to run this jar requires the following environment variables:
# WORKSPACE_PATH, ECLIPSE_TEMPLATES_PATH and REPLACEMENT_PATTERNS_PATH
echo $OASP_WORKSPACE_REL_PATH
echo $OASP_ECLIPSE_TEMPLATES_REL_PATH
WORKSPACE_PATH=$OASP_WORKSPACE_REL_PATH \
  ECLIPSE_TEMPLATES_PATH=$OASP_WORKSPACE_REL_PATH/$OASP_SETTINGS_REL_PATH/$OASP_ECLIPSE_TEMPLATES_REL_PATH \
  REPLACEMENT_PATTERNS_PATH=$OASP_ECLIPSE_REPLACEMENT_PATTERNS_PATH \
  java -jar $OASP_SCRIPTS_PATH/$OASP4J_ECLIPSE_CONFIGURATOR -u
echo "Eclipse preferences for workspace: $OASP_WORKSPACE have been created/updated"


popd > /dev/null