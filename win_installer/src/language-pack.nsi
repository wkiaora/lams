/****************************************************************
 * Copyright (C) 2006 LAMS Foundation (http://lamsfoundation.org)
 * =============================================================
 * License Information: http://lamsfoundation.org/licensing/lams/2.0/
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2.0
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 * USA
 *
 * http://www.gnu.org/licenses/gpl.txt
 * ****************************************************************
 * Coded by Luke Foxton (lfoxton@melcoe.mq.edu.au)
 */

# includes
#!include "TextFunc.nsh"
!include "Functions.nsh"
!include "MUI.nsh"
!include "LogicLib.nsh"

# constants
!define VERSION "2006-12-12" ; DATE of language pack in fromat YYYYMMDD
!define SOURCE_JBOSS_HOME "D:\jboss-4.0.2"  ; location of jboss where lams was deployed
!define REG_HEAD "Software\LAMS Foundation\LAMSv2"

# installer settings
!define MUI_ICON "..\graphics\lams2.ico"

Name "LAMS ${VERSION} Language Pack Update"

# Installer attributes
OutFile "..\build\LAMSLanguagePack-${VERSION}.exe"
InstallDir "C:\lams"
Icon ..\graphics\lams2.ico
InstallDirRegKey HKLM "${REG_HEAD}" ""
LicenseForceSelection radiobuttons "I Agree" "I Do Not Agree" 
VIProductVersion 2.0.0.0
VIAddVersionKey ProductName "LAMS Language Pack ${VERSION}"
VIAddVersionKey ProductVersion "${VERSION}"
VIAddVersionKey CompanyName "${COMPANY}"
VIAddVersionKey CompanyWebsite "${URL}"
VIAddVersionKey FileVersion ""
VIAddVersionKey FileDescription ""
VIAddVersionKey LegalCopyright ""

# set warning when cancelling install
!define MUI_ABORTWARNING

# set welcome page
!define MUI_WELCOMEPAGE_TITLE "LAMS ${VERSION} Install Wizard"
!define MUI_WELCOMEPAGE_TEXT 'This wizard will guide you through updating your LAMS 2.0 with the latest language packs available.\r\n\r\n\
                              This language pack requires that you have a working version of LAMS 2.0 installed\n\n\
                              It is recommended that you close all other programs before continuing with the installation\r\n\r\n\
                              Click next to continue'

# set components page type
#!define MUI_COMPONENTSPAGE_SMALLDESC
!define MUI_COMPONENTSPAGE_NODESC 
!define MUI_COMPONENTSPAGE_TEXT 'Existing language files will be backed up in:$\n\
                                 $\t "(lams install directory)\jboss-4.0.2\server\default\deploy\lams.ear\lams-dictionary.jar\lams-dictionary.zip"$\n$\n\
                                 Language Files will then be overwritten by the new LAMS Language Pack ${VERSION}'

# set instfiles page to wait when done
!define MUI_FINISHPAGE_NOAUTOCLOSE
!define MUI_UNFINISHPAGE_NOAUTOCLOSE

# display finish page stuff
!define MUI_FINISHPAGE_RUN $INSTDIR\lams-start.exe
!define MUI_FINISHPAGE_RUN_TEXT "Start LAMS now"
!define MUI_FINISHPAGE_TEXT "The LAMS Server has been successfully installed on your computer."
!define MUI_FINISHPAGE_SHOWREADME $INSTDIR\readme.txt
;!define MUI_FINISHPAGE_SHOWREADME_TEXT "Open the readme file"
!define MUI_FINISHPAGE_LINK "Visit LAMS Community"
!define MUI_FINISHPAGE_LINK_LOCATION "http://www.lamscommunity.org"


# installer screen pages
;--------------------------------
!insertmacro MUI_PAGE_WELCOME
!insertmacro MUI_PAGE_LICENSE "..\license.txt"
!insertmacro MUI_PAGE_COMPONENTS
!insertmacro MUI_PAGE_INSTFILES
!insertmacro MUI_PAGE_FINISH
;--------------------------------

# supported translations
!insertmacro MUI_LANGUAGE "English" # first language is the default language
/*  
  !insertmacro MUI_LANGUAGE "French"
  !insertmacro MUI_LANGUAGE "German"
  !insertmacro MUI_LANGUAGE "Spanish"
  !insertmacro MUI_LANGUAGE "SimpChinese"
  !insertmacro MUI_LANGUAGE "Korean"
  !insertmacro MUI_LANGUAGE "Italian"
  !insertmacro MUI_LANGUAGE "Dutch"
  !insertmacro MUI_LANGUAGE "Danish"
  !insertmacro MUI_LANGUAGE "Norwegian"
  !insertmacro MUI_LANGUAGE "NorwegianNynorsk"  
  !insertmacro MUI_LANGUAGE "Portuguese"
  !insertmacro MUI_LANGUAGE "PortugueseBR"
  !insertmacro MUI_LANGUAGE "Greek"
  !insertmacro MUI_LANGUAGE "Russian"
  !insertmacro MUI_LANGUAGE "Polish"
  !insertmacro MUI_LANGUAGE "Bulgarian"  
  !insertmacro MUI_LANGUAGE "Thai"
  !insertmacro MUI_LANGUAGE "Arabic"
  !insertmacro MUI_LANGUAGE "TradChinese"
  !insertmacro MUI_LANGUAGE "Japanese"
  !insertmacro MUI_LANGUAGE "Swedish"
  !insertmacro MUI_LANGUAGE "Finnish"
  !insertmacro MUI_LANGUAGE "Ukrainian"
  !insertmacro MUI_LANGUAGE "Czech"
  !insertmacro MUI_LANGUAGE "Slovak"
  !insertmacro MUI_LANGUAGE "Croatian"
  !insertmacro MUI_LANGUAGE "Hungarian"
  !insertmacro MUI_LANGUAGE "Romanian"
  !insertmacro MUI_LANGUAGE "Latvian"
  !insertmacro MUI_LANGUAGE "Macedonian"
  !insertmacro MUI_LANGUAGE "Estonian"
  !insertmacro MUI_LANGUAGE "Turkish"
  !insertmacro MUI_LANGUAGE "Lithuanian"
  !insertmacro MUI_LANGUAGE "Catalan"
  !insertmacro MUI_LANGUAGE "Slovenian"
  !insertmacro MUI_LANGUAGE "Serbian"
  !insertmacro MUI_LANGUAGE "SerbianLatin"
  !insertmacro MUI_LANGUAGE "Farsi"
  !insertmacro MUI_LANGUAGE "Hebrew"
  !insertmacro MUI_LANGUAGE "Indonesian"
  !insertmacro MUI_LANGUAGE "Mongolian"
  !insertmacro MUI_LANGUAGE "Luxembourgish"
  !insertmacro MUI_LANGUAGE "Albanian"
  !insertmacro MUI_LANGUAGE "Breton"
  !insertmacro MUI_LANGUAGE "Belarusian"
  !insertmacro MUI_LANGUAGE "Icelandic"
  !insertmacro MUI_LANGUAGE "Malay"
  !insertmacro MUI_LANGUAGE "Bosnian"
  !insertmacro MUI_LANGUAGE "Kurdish"
  !insertmacro MUI_LANGUAGE "Irish"
*/

# Variables
;--------------------------------
Var BACKUP_DIR
Var LAMS_DIR
Var VERSION_INT
Var FLASHXML_DIR
;--------------------------------


Section "LAMS Language Pack ${VERSION}" LanguagePack
    # write this language pack version to registry
    WriteRegStr HKLM "${REG_HEAD}" "language_pack" $VERSION_INT
    Detailprint 'Writing Language pack version ${VERSION} to registry: "${REG_HEAD}"'
    
    setoutpath $EXEDIR
    File /r "..\zip"

    ;backup existing language files
    call zipLanguages
    
    ; copy language files from LAMS projects to a folder in $INSTDIR
    call copyProjects
    
    ; copy the flash dictionary files from central/web/flashxml to: 
    ; <JBoss_dir>\server\default\lams.ear\lams-central.war\flashxml
    call copyFlashxml
    
    
    ####################
    # TODO  Work out what language files to copy to the 'library' directory
    # TODO  Copy the flshxml files
    ####################
    
    #lams_blah\conf\language\*.properties
    # lams_central\flashxml\*
SectionEnd

;--------------------------------
;Descriptions
  ;Language strings
  LangString DESC_LanguagePack ${LANG_ENGLISH} "LAMS 2.0 Language pack update ${VERSION} "

  ;Assign language strings to sections
  !insertmacro MUI_FUNCTION_DESCRIPTION_BEGIN
    !insertmacro MUI_DESCRIPTION_TEXT ${LanguagePack} $(DESC_LanguagePack)
  !insertmacro MUI_FUNCTION_DESCRIPTION_END
;--------------------------------

# Installer functions
Function .onInit
    InitPluginsDir
    # select language
    ;!insertmacro MUI_LANGDLL_DISPLAY
    
    #get the version in from the version date yyyy-mm-dd
    call getVersionInt
    
    # Abort install if already installed or if a newer version is installed
    ReadRegStr $0 HKLM "${REG_HEAD}" "language_pack"
    ${VersionCompare} "$VERSION_INT" "$0" $1
    ${If} $1 == "0"
        MessageBox MB_OK|MB_ICONSTOP "You already have LAMS-LanguagePack-${VERSION} installed"
        Abort
    ${EndIf}    
    ${if} $1 == "2"
        MessageBox MB_OK|MB_ICONSTOP "Your current language pack is a newer version than this version: LAMS-LanguagePack-$0"
        Abort
    ${EndIf}
    
    # Abort if there is no version of LAMS2 installed
    ReadRegStr $0 HKLM "${REG_HEAD}" "version"
    ${If} $0 = ""
        MessageBox MB_OK|MB_ICONSTOP "No version of LAMS 2.x is installed$\n$\n\
                                      Please install LAMS 2 before continuing"
        Abort
    ${EndIf}
    
    #set the installation directory
    ReadRegStr $0 HKLM "${REG_HEAD}" "dir_inst"
    strcpy $LAMS_DIR $0
    strcpy $INSTDIR "$0\jboss-4.0.2\server\default\deploy\lams.ear\lams-dictionary.jar\org\lamsfoundation\lams"
FunctionEnd

Function .onInstSuccess
    RMDir /r "$EXEDIR\zip"
FunctionEnd

;backup existing language files 
;zip to $INSTDIR\jboss-4.0.2\server\default\deploy\lams.ear\lams-dictionary.jar\lams-dictionary-bak.zip
Function zipLanguages
    strcpy $BACKUP_DIR "$LAMS_DIR\jboss-4.0.2\server\default\deploy\lams.ear\lams-dictionary.jar\backup"
    
    detailprint 'Zipping existing files to "$BACKUP_DIR"' 
    
    #zip existing language files
    setoutpath $INSTDIR
    rmdir /r "$BACKUP_DIR"
    createdirectory "$BACKUP_DIR"
    Strcpy $4 '$EXEDIR\zip\7za.exe a -r -tzip "$BACKUP_DIR\lamsDictionaryBak.zip" "*"'
    nsExec::ExecToStack $4 
    pop $8
    pop $9
    detailprint $8
    detailprint $9
    detailprint 'backupdir: $BACKUP_DIR'
    detailprint 'instdir: $INSTDIR'
    detailprint '$4'
    detailprint 'done'
FunctionEnd

Function getVersionInt
    push ${VERSION}
    push "-"
    push 0
    push 1
    call Strtok
    pop $VERSION_INT
    
    push ${VERSION}
    push "-"
    push 1
    push 1
    call Strtok
    pop $0
    strcpy $VERSION_INT "$VERSION_INT$0"
    
    push ${VERSION}
    push "-"
    push 2
    push 1
    call Strtok
    pop $0
    strcpy $VERSION_INT "$VERSION_INT$0"
FunctionEnd

; copies all the lams_blah project language files from lams_blah/conf/languages
; files are compresses then extracted to the jboss language directory:
; C:\lams\jboss-4.0.2\server\default\deploy\lams.ear\lams-dictionary.jar\org\lamsfoundation\lams
Function copyProjects
    
    ;copying COMMON project language files
    setoutpath "$INSTDIR"
    detailprint "Extracting language files for lams_common"
    file /a "..\..\lams_common\conf\language\*"
    
    
    ;copying ADMIN project language files
    setoutpath "$INSTDIR\admin"
    detailprint "Extracting language files for lams_admin"
    file /a "..\..\lams_admin\conf\language\*"
    
    ;copying CENTRAL project language files
    setoutpath "$INSTDIR\central"
    detailprint "Extracting language files for lams_central"
    file /a "..\..\lams_central\conf\language\*"
    
    ;copying CONTENTREPOSITORY project language files
    setoutpath "$INSTDIR\contentrepository"
    detailprint "Extracting language files for lams_contentrepository"
    file /a  "..\..\lams_contentrepository\conf\language\*"
    
    ;copying LEARNING project language files
    setoutpath "$INSTDIR\learning"
    detailprint "Extracting language files for lams_learning"
    file /a "..\..\lams_learning\conf\language\*"
     
    ;copying MONITORING project language files
    setoutpath "$INSTDIR\monitoring"
    detailprint "Extracting language files for lams_monitoring"
    file /a  "..\..\lams_monitoring\conf\language\*"
    
    ;copying TOOL_CHAT project language files
    setoutpath "$INSTDIR\tool\chat"
    detailprint "Extracting language files for lams_tool_chat"
    file /a "..\..\lams_tool_chat\conf\language\*"
    
    ;copying TOOL_FORUM project language files
    setoutpath "$INSTDIR\tool\forum"
    detailprint "Extracting language files for lams_tool_forum"
    file /a "..\..\lams_tool_forum\conf\language\*"
    
    ;copying TOOL_LAMC project language files
    setoutpath "$INSTDIR\tool\mc"
    detailprint "Extracting language files for lams_tool_lamc"
    file /a "..\..\lams_tool_lamc\conf\language\*"
    
    ;copying TOOL_LAQA project language files
    setoutpath "$INSTDIR\tool\qa"
    detailprint "Extracting language files for lams_tool_laqa"
    file /a "..\..\lams_tool_laqa\conf\language\*"
    
    ;copying TOOL_NOTEBOOK project language files
    setoutpath "$INSTDIR\tool\notebook"
    detailprint "Extracting language files for lams_tool_notebook"
    file /a "..\..\lams_tool_notebook\conf\language\*"
    
    ;copying TOOL_NB project language files
    setoutpath "$INSTDIR\tool\noticeboard"
    detailprint "Extracting language files for lams_tool_nb"
    file /a "..\..\lams_tool_nb\conf\language\*"
    
    ;copying TOOL_LARSRC project language files
    setoutpath "$INSTDIR\tool\rsrc"
    detailprint "Extracting language files for lams_tool_larsrc"
    file /a "..\..\lams_tool_larsrc\conf\language\*"
    
    ;copying TOOL_SBMT project language files
    setoutpath "$INSTDIR\tool\sbmt"
    detailprint "Extracting language files for lams_tool_sbmt"
    file /a "..\..\lams_tool_sbmt\conf\language\*"
    
    ;copying TOOL_SCRIBE project language files
    setoutpath "$INSTDIR\tool\scribe"
    detailprint "Extracting language files for lams_tool_scribe"
    file /a "..\..\lams_tool_scribe\conf\language\*"
    
    ;copying TOOL_SURVEY project language files
    setoutpath "$INSTDIR\tool\survey"
    detailprint "Extracting language files for lams_tool_survey"
    file /a "..\..\lams_tool_survey\conf\language\*"
    
    ;copying TOOL_VOTE project language files
    setoutpath "$INSTDIR\tool\vote"
    detailprint "Extracting language files for lams_tool_vote"
    file /a "..\..\lams_tool_vote\conf\language\*"
    
FunctionEnd


; copys the files from lams_central/web/flashxml to:
; "<JBOSS>/\server\default\lams.ear\lams-central.war\flashxml
Function copyFlashxml
    strcpy $FLASHXML_DIR "$LAMS_DIR\jboss-4.0.2\server\default\deploy\lams.ear\lams-central.war\flashxml"

    setoutpath $FLASHXML_DIR
    detailprint "Extracting language files for FLASH"
    file /a /r /x "CVS" "..\..\lams_central\web\flashxml\*"
    detailprint "DONE!"
    
FunctionEnd
