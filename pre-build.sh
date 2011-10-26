#!/usr/bin/env bash                                                             

MANIFEST="AndroidManifest.xml"                                                  

if [ -f $MANIFEST ]                                                             
then                                                                            
    LINE=$(grep -o ${MANIFEST} -e 'android:versionCode="[0-9]*"');              
    declare -a LINE;                                                            
    LINE=(`echo $LINE | tr "\"" " "`);                                          
    VERSION=(${LINE[1]});                                                       
    INCREMENTED=$(($VERSION+1))                                                 
    sed "s/android:versionCode=\"[0-9]*\"/android:versionCode=\"${INCREMENTED}\"/" $MANIFEST > $MANIFEST.tmp && mv $MANIFEST.tmp $MANIFEST
    git add $MANIFEST                                                           
    echo "Updated android:versionCode to ${INCREMENTED} in ${MANIFEST}";        
fi