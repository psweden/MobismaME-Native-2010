package MControll;

/**
 * <p>Title: Mobile Extension</p>
 *
 * <p>Description:(Native) Panasonic PBX Include </p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: mobisma ab</p>
 *
 * @author Peter Albertsson
 * @version 2.0
 */
/*Import av java-paket*/
/* DataStore */
import java.io.*;

import javax.microedition.lcdui.*;
import javax.microedition.midlet.MIDlet;
import javax.microedition.rms.*;

/* Model */
import MModel.Date_Time;
import MView.*;
import MDataStore.DataBase_RMS;
import MModel.CONF;
import MModel.CONF_settings;
import MModel.ConnectTCPIP_Socket;
import MModel.Methods;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;
import MModel.SortClass;
import MModel.InternNumber;


/* Klassen Main_Controll startar här. */
public class Main_Controll extends MIDlet implements ItemStateListener,
        CommandListener,

        Runnable {
    /*
     - Konfigueringsfilen CONF och Language --- initierar programmets
         - Språk, Ikoner och annan information.
     */
    private MModel.CONF conf;
    private MModel.CONF_settings conf_S;
    private MModel.Language language;
    private MModel.Date_Time dateTime;
    private MDataStore.DataBase_RMS rms;
    private MView.AboutUs aboutUs;
    private MView.HelpInfo helpInfo;
    private MView.ServerNumber serverNumber;
    private MModel.ConnectTCPIP_Socket tcpip;
    private MModel.Methods methods;

    // Portnummer för TCP/IP connection
    private static String url = "socket://127.0.0.1:8100";

    public static final int CONFDATA = 1;
    public static final int LOGDATA = 2;
    public static final int IMEIDATA = 3;
    public static final int LOGSIZE = 4;

    private static String inturl = "socket://127.0.0.1:8100";
    private static String ext_url =
            "http://www.mobisma.com:80/socketapi/mobilesock.php";
    private static String confdata;
    private static String logdata;
    private static String imei;
    private static int logfilesize;
    private static int icount;
    private static String logrequest;
    private int requestwhat;
    public ChoiceGroup radioButtons = new ChoiceGroup("", Choice.EXCLUSIVE);
    private ChoiceGroup editButtons = new ChoiceGroup("", Choice.EXCLUSIVE);
    private int defaultIndex, editButtonIndex;
    public int IDInternNumber;

    private boolean isInitialized;
    private boolean splashIsShown;

    // Public strängar.
    public String
            checkAlert,
    ViewDateString,
    editNEWAbsent,
    editHHTTMMTT = "",
    mexOnOff,
    absentChooseOneTwo,
    allCallForwardRename = "",
    externCallForwardRename = "",
    internCallForwardRename = "",
    absentSystemName = "",
    absentTempName = "";

    /* PBX Settings */
    public String
            lineAccess_PBX,
    switchBoardNumber_PBX,
    countryCode_PBX,
    extensionNumber_PBX,
    HGP_PBX,
    pinCodeNumber_PBX,
    precode_PBX,
    voiceMailSwitchboard_PBX,
    voiceMailOperator_PBX,
    mexONOFF_PBX,
    checkStatus_PBX,
    dbg_PBX,
    demo_PBX,
    companyName_PBX,
    userName_PBX,
    countryName_PBX,
    iconNumber_PBX,
    lang_PBX,
    pbx_ID,
    prg_Name,
    CheckTwo,
    pbx_type,
    default_lang,
    eng_lang,
    absentStatus,
    device_brands,
    deveice_model,
    pbx_name,
    emptyPrecense = "",

    // Ny hänvisning
    editAbsentName_1,
    editAbsentName_2,
    editAbsentName_3,
    editAbsentDTMF_1,
    editAbsentDTMF_2,
    editAbsentDTMF_3,
    setAbsentNAMEString,
    edit_HHMM_TTMM_1,
    edit_HHMM_TTMM_2,
    edit_HHMM_TTMM_3;

    private Ticker absentStatusTicker;

    private String
            // TCP/IP connection
            request,
    ResponceMessage,

    /*Tid och Datum*/
    setYear_30DAY, setDay_30DAY, setMounth_30DAY, setMounthName_30DAY,
    setDay_TODAY, setMounth_TODAY, setYear_TODAY, setMounthNameToday;

    /* Listor/Menyer i prg */
    public List

            debug_List, linePrefix_List, pbx_List_type,
    pbx_List, mainList, language_List, absentList, absentEditList,
    groupList, voiceMailPBXList,

    /* Callforward list */
    callForwardList, allCallForwardList,
    externCallForwardList, internCallForwardList;

    /* Övriga settings som Display, Alert, Commands och Form osv. */
    private Display display, exitDisplay;

    public Alert alertEditSettings, alertRestarting,
    alertExitRedButton, alertON, alertOFF, alertMexAlreadyONOFF,
    alertDebugONOFF, alertSendOKNOK, alertSENDDebug, alertLogOutDebug,
    alertExperinceLicense, alertExit, alert;

    private Form

    // AutoAccess settings.
    AutoAccessSettingsForm,
    AutoAccessSettingsNOPrefixForm,

    // PIN-Code settings
    pinCodeSettingsForm,

    // Koppla samtal
    connectPhoneForm, connectEditForm,
    connectRenameForm, connectRenameEditForm,

    // Pre-edit code
    preEditForm,

    // Hänvisningsformer.

    atExtForm, // - Finns på anknytning
    backAtForm, // - Åter klockan
    outForm, // - Åter den


    // Editera ny hänvisning
    editAbsentForm, // - Lägg till ny hänvisning

    // Grupp former.
    loginGroupForm, logoffGroupForm,

    // PBX Röstbrevlåda form.
    voiceEditForm_PBX,

    // Operatör Röstbrevlåda (resarverar ett nummer)
    voiceOperatorMessageForm,

    // Ange landsnummer form.
    countryForm,

    // Vidarekoppling
    allCallForwardForm, externCallForwardForm, internCallForwardForm,

    // Log-data till server
    logDataForm;

    private Command
            // AutoAccess kommandon.
            AutoAccessBackCommand, AutoAccessCancelCommand,
    AutoAccessSaveCommand,
    AutoAccessSaveNOPrefixCommand, AutoAccessBackNOPrefixCommand,
    AutoAccessCancelNOPrefixCommand,

    // PIN-Code kommandon
    pinCodeSaveCommand, pinCodeBackCommand, pinCodeCancelCommand,

    // Pre-edit Code kommandon
    preEditSaveCommand, preEditBackCommand, preEditCancelCommand,

    // Koppla samtal kommando
    connectSendCommand, connectBackCommand, connectEditRenameCommand,
    connectEditBackCommand, connectEditCancelCommand,
    connectEditSaveCommand, connectRenameBackCommand,
    connectEditRenameBackCommand, connectEditRenameCancelCommand,
    connectEditRenameSaveCommand, connectResumeCommand,

    // Vidarekoppla kommando
    allCallForwardSendCommand, allCallForwardBackCommand,
    externCallForwardSendCommand, externCallForwardBackCommand,
    internCallForwardSendCommand, internCallForwardBackCommand,

    backCallForwardCommand, backAllCallForwardCommand,
    backExternCallForwardCommand, backInternCallForwardCommand,

    // Röstbrevlåda PBX, kommandon
    voiceEditSaveCommand_PBX, voiceEditBackcommand_PBX,
    voiceEditCancelCommand_PBX,

    // Röstbrevlåda Operatör, kommandon
    voiceOperatorMessageSaveCommand, voiceOperatorMessageCancelCommand,
    voiceOperatorMessageBackCommand,

    // Röstbrevlådan listkommando
    voiceMailPBXListBackCommand,

    // Country kommandon
    countryBackCommand, countryCancelCommand, countrySaveCommand,

    // MainList kommandon
    mainListEditCommand, mainListaboutMobismaCommand, mainListExitCommand,

    // Hänvisning kommandon
    BackCommandAbsentList, // - kommando till huvudmenyn för hänvisning.

    atExtBackCommand, atExtSendCommand, // - Finns på anknytning
    backAtBackCommand, backAtSendCommand, // - Tillbaka klockan
    outBackCommand, outSendCommand, // - Åter den

    // - Ta bort hänvisning se absentList plats 6.

    // Editera ny hänvisning (Form) kommandon
    editAbsentBackCommand, editAbsentSaveCommand,
    editAbsentCancelCommand,

    // Editera ny hänvisning (List) kommandon
    editAbsentListBackCommand,
    editAbsentListCancelCommand,

    // Grupp kommandon
    groupBackCommand, loginGroupSendCommand,
    loginGroupBackCommand, logoffGroupSendCommand,
    logoffGroupBackCommand,

    // Fristående kommandon.
    GraphicsBackCommand, goGraphicsBackCommand,
    GraphicsAboutCommand, GraphicsHelpCommand,

    // pbx_List kommando.
    pbx_ListCancelCommand,

    // pbx_List_type kommandon.
    pbx_List_typeBackCommand, pbx_List_typeCancelCommand,

    // language_List kommando
    languageListBackCommand,

    // Alert-Exit 'confirm' Ja eller Nej
    confirmExitYESCommand, confirmExitNOCommand,
    confirmOnYESCommand, confirmOnCancelCommand,
    confirmOffYESCommand, confirmOffCancelCommand,

    // Alert-Experince Licens
    licensYESCommand,

    // kommando för trådarna RUN.
    thCmd,

    // Kommando för vidarekoppling

    // Log-data till server
    logDataLogInCommand,
    logDataCancelLogInCommand, debugListLogOutCommand,

    // linePrefix_List
    linePrefixBackCommand;

    public TextField
            // Koppla samtal textfält
            connectTextField,
    connectEditNameTextField, connectEditExtensionTextField,
    connectEditRenameNameTextField, connectEditRenameExtensionTextField,

    // Pre-edit Code textfält
    preEditTextField,

    // HänvisningsTextField.

    atExtTextField, // - Finns på anknytning
    backAtTextField, // - Tillbaka klockan
    outTextField, // - Åter den

    newAbsent_1TextField, // - Ny hänvisning
    newAbsent_2TextField, // - Ny hänvisning
    newAbsent_3TextField, // - Ny hänvisning

    // Editera ny hänvisning
    editAbsentName_TextField, // - Lägg till namn för ny hänvisning

    // AutoAccess textfält tillhör AutoAccessSettingsForm
    AutoAccessLineAccessTextField, AutoAccessSwitchBoardTextField,
    AutoAccessNOPrefixSwitchBoardTextField,

    // PIN-Coide textfält tillhör pinCodeSettingsForm
    pinCodeLineAccessTextField, pinCodeSwitchBoardNumberTextField,
    pinCodeExtensionNumberTextField, pinCodePinCodeNumberTextField,


    // PBX'ns textfält för röstbrevlåda tillhör voiceEditForm_PBX
    voiceMailPBXTextField_PBX,

    // Operatör voicemail tillhör voiceOperatorMessageForm
    voiceOperatorMessageTextField,

    // Textfältet tillhör countryForm
    countryTextField,

    // Textfältet tillhör loginGroupForm
    loginGroupTextField,

    // Textfältet tillhör logoffGroupForm
    logoffGroupTextField,

    // Vidarekoppling
    allCallForwardTextField, externCallForwardTextField,
    internCallForwardTextField,

    // Textfält vidarekoppling

    // Log-data till server
    logDataTextField;

    //================== D E F A U L T - Språk =================================

    public String

            settingsDefaultAbout_DEF,
    SettingsDefaultAccessPINcode_DEF,
    extensionDefaultAddNew_DEF,
    callForwardDefaultAllCalls_DEF,
    absentDefaultAtExt_DEF,
    settingsDefaultAutoAccess_DEF,
    genDefaultBack_DEF,
    absentDefaultBackAt_DEF,
    callForwardDefaultBusy_DEF,
    callForwardDefaultBusyNoAnswer_DEF,
    callDefaultCall_DEF,
    extensionDefaultCall_DEF,
    callForwardDefault_DEF,
    msgDefaultCallistIsEmpty_DEF,
    genDefaultCancel_DEF,
    alertDefaultCantAddAnymoreExt_DEF,
    alertDefaultCouldntAddChangesEmtpyField_DEF,
    alertDefaultCouldntAddRecord_DEF,
    alsertDefaultChangesSave_DEF,
    mgsDefaultContactListIsEmpty_DEF,
    alertDefaultCountryCodeError_DEF,
    settingsDefaultCountryCode_DEF,
    genDefaultDelete_DEF,
    genDefaultDeleteAll_DEF,
    dialledCallsDefault_DEF,
    callForwardDefaultDontDisturb_DEF,
    genDefaultEdit_DEF,
    settingsDefaultEditPBXAccess_DEF,
    absentDefaultEditPresence_DEF,
    voiceMailDefaultEditVoicemail_DEF,
    enterDefaultEngerCharacter_DEF,
    enterDefaultEnterExtension_DEF,
    enterDefaultEnterGroupNumber_DEF,
    enterDefaultEnterHHMM_DEF,
    enterDefaultEnterNumber_DEF,
    alertDefaultErrorChangeTo_DEF,
    alertDefaultError_DEF,
    enterDefaultEnterMMDD_DEF,
    exitDefaultExitTheProgramYesOrNo_DEF,
    genDefaultExit_DEF,
    settingsDefaultExtension_DEF,
    callExtensionDefaultExtensionWasAdded_DEF,
    callForwardDefaultExternCalls_DEF,
    absentDefaultGoneHome_DEF,
    groupsDefaultGroups_DEF,
    settingsDefaultHelp_DEF,
    absentDefaultInAMeeting_DEF,
    alertDefaultInfo_DEF,
    alertDefaultInstedOf_DEF,
    callForwardDefaultInternCalls_DEF,
    settingsDefaultLanguage_DEF,
    settingsDefaultLineAccess_DEF,
    groupsDefaultLoginAllGroups_DEF,
    groupsDefaultLoginSpecificGroup_DEF,
    groupsDefaultLogoutAllGroups_DEF,
    groupsDefaultLogoutSpecificGroup_DEF,
    alertDefaultMaxSize_DEF,
    genDefaultMinimise_DEF,
    callExtensionDefaultName_DEF,
    exitDefaultNo_DEF,
    callForwardDefaultNoAnswer_DEF,
    settingsDefaultOptions_DEF,
    absentDefaultOutUntil_DEF,
    absentDefaultPersonalAtt_DEF,
    settingsDefaultPINcode_DEF,
    settingsDefaultPreEditCode_DEF,
    callForwardDefaultRemove_DEF,
    absentDefaultRemovePresence_DEF,
    exitDefaultRestartProgram_DEF,
    alertDefaultSaveChanges_DEF,
    genDefaultSave_DEF,
    settingsDefaultSelectCountryCode_DEF,
    genDefaultSelect_DEF,
    genDefaultSend_DEF,
    absentDefaultSetPresence_DEF,
    settingsDefaultSettings_DEF,
    callExtensionDefaultSurname_DEF,
    settingsDefaultSwitchboardNumber_DEF,
    absentDefaultSystemAttOne_DEF,
    absentDeafaultSystemAttTwo_DEF,
    absentDeafaultWillReturnSoon_DEF,
    alertDefaultWrongInputTryAgain_DEF,
    voiceMailDefaultVoiceMail_DEF,
    genDefaultYes_DEF,

    accessPBXDefault_DEF,
    autoAccessDefault_DEF,
    accessViaPINCodeDefault_DEF,
    dialDefault_DEF,

    alertExitMEXOnMessage_DEF,
    AlertMessageExitText_DEF,
    alertMessageMEXOn_DEF,
    alertMessageMEXOff_DEF,
    alertMessageMexServerInfo_DEF,
    alertMessageMexAlreadyON_DEF,
    alertMessageMexAlreadyOFF_DEF,
    mainListAttributMexOn_DEF,
    mainListAttributMexOff_DEF,
    operatorVoicemail_DEF,

    absentTimeOfReturn_DEF,
    absentDateOfReturn_DEF,
    absentLunch_DEF,
    absentMeeting_DEF,
    absentVacation_DEF,
    absentIllness_DEF,

    callForwardTransfer_DEF,
    callForwardPermForward_DEF,
    callForwardInterForward_DEF,
    callForwardExternForward_DEF,
    callForwardCancelExtern_DEF,
    callForwardCancelPermIntern_DEF,

    transferBack_DEF,

    textYourNumber_DEF,
    textNewNumber_DEF,

    voiceMailActivate_DEF,
    voiceMailDeActivate_DEF,
    voiceMailListen_DEF,

    version_DEF,
    system_DEF,

    programExitON_DEF,
    programExitOFF_DEF;


    /* Konstruktorn startar här. */

    public Main_Controll() throws InvalidRecordIDException,
            RecordStoreNotOpenException, RecordStoreException, IOException {

        //------------ kontrollerar om CONF.java är satt -----------------------
        MModel.CONF_settings conf_S = new CONF_settings();
        conf_S.setConfSettings();
        conf_S = null;

        // --- SplashScreen
        display = Display.getDisplay(this);

        /*TCP_IP_SOCKET*/
        this.ResponceMessage = "";

        /*RMS DB objekt hämtar in egenskaperna här*/
        MDataStore.DataBase_RMS rms = new DataBase_RMS();

        /* PBX Settings, attribut från RMS DB */
        this.lineAccess_PBX = rms.lineAccess_PBX;
        this.HGP_PBX = rms.HGP_PBX;
        this.precode_PBX = rms.precode_PBX;
        this.voiceMailSwitchboard_PBX = rms.voiceMailSwitchboard_PBX;
        this.switchBoardNumber_PBX = rms.switchBoardNumber_PBX;
        this.countryCode_PBX = rms.countryCode_PBX;
        this.extensionNumber_PBX = rms.extensionNumber_PBX;
        this.pinCodeNumber_PBX = rms.pinCodeNumber_PBX;
        this.voiceMailOperator_PBX = rms.voiceMailOperator_PBX;
        this.mexONOFF_PBX = rms.mexONOFF_PBX;
        this.checkStatus_PBX = rms.checkStatus_PBX;
        this.dbg_PBX = rms.dbg_PBX;
        this.demo_PBX = rms.demo_PBX;
        this.companyName_PBX = rms.companyName_PBX;
        this.userName_PBX = rms.userName_PBX;
        this.countryName_PBX = rms.countryName_PBX;
        this.iconNumber_PBX = rms.iconNumber_PBX;
        this.lang_PBX = rms.lang_PBX;
        this.CheckTwo = rms.CheckTwo;
        this.pbx_ID = rms.getPbxID();
        this.prg_Name = rms.getPrgName();
        this.pbx_type = rms.getPBXType();
        this.default_lang = rms.getDefaultLanguage();
        this.eng_lang = rms.getENGlang();
        this.absentStatus = rms.getAbsentStatus();
        this.device_brands = rms.getPhoneBrands();
        this.deveice_model = rms.getPhoneModel();
        this.pbx_name = rms.getPbxName();

        // Ny hänvisning
        this.editAbsentName_1 = rms.getEditAbsentName_1();
        this.editAbsentName_2 = rms.getEditAbsentName_2();
        this.editAbsentName_3 = rms.getEditAbsentName_3();
        this.editAbsentDTMF_1 = rms.getEditAbsentDTMF_1();
        this.editAbsentDTMF_2 = rms.getEditAbsentDTMF_2();
        this.editAbsentDTMF_3 = rms.getEditAbsentDTMF_3();
        this.edit_HHMM_TTMM_1 = rms.getHHMMTTMM_1();
        this.edit_HHMM_TTMM_2 = rms.getHHMMTTMM_2();
        this.edit_HHMM_TTMM_3 = rms.getHHMMTTMM_3();

        /*Datum och Tid objekt hämtar in egenskaperna här*/
        MModel.Date_Time dateTime = new Date_Time();

        /*Settings för metoden ControllDateTime() */
        this.setDay_30DAY = rms.getDay_30_DAY();
        this.setMounth_30DAY = rms.getMounth_30_DAY();
        this.setMounthName_30DAY = dateTime.setMounth(setMounth_30DAY);
        this.setYear_30DAY = rms.getYear_30_DAY();

        this.setDay_TODAY = rms.getDay_TODAY();
        this.setMounth_TODAY = rms.getMounth_TODAY();
        this.setMounthNameToday = rms.getMounth_TODAY();
        this.setMounthNameToday = dateTime.setMounth(setMounth_TODAY);
        this.setYear_TODAY = rms.getYear_TODAY();

        /* ================== SPRÅK =============================  */

        // --- Default språk, Engelska. Alltid andraspråk i prg.

        if (this.lang_PBX.equals("2")) { // English >> Default

            settingsDefaultAbout_DEF = language.settingsDefaultAbout_1;
            SettingsDefaultAccessPINcode_DEF = language.
                                               SettingsDefaultAccessPINcode_1;
            extensionDefaultAddNew_DEF = language.extensionDefaultAddNew_1;
            callForwardDefaultAllCalls_DEF = language.
                                             callForwardDefaultAllCalls_1;
            absentDefaultAtExt_DEF = language.absentDefaultAtExt_1;
            settingsDefaultAutoAccess_DEF = language.
                                            settingsDefaultAutoAccess_1;
            genDefaultBack_DEF = language.genDefaultBack_1;
            absentDefaultBackAt_DEF = language.absentDefaultBackAt_1;
            callForwardDefaultBusy_DEF = language.callForwardDefaultBusy_1;
            callForwardDefaultBusyNoAnswer_DEF =
                    language.callForwardDefaultBusyNoAnswer_1;
            callDefaultCall_DEF = language.callDefaultCall_1;
            extensionDefaultCall_DEF = language.extensionDefaultCall_1;
            callForwardDefault_DEF = language.callForwardDefault_1;
            msgDefaultCallistIsEmpty_DEF = language.msgDefaultCallistIsEmpty_1;
            genDefaultCancel_DEF = language.genDefaultCancel_1;
            alertDefaultCantAddAnymoreExt_DEF =
                    language.alertDefaultCantAddAnymoreExt_1;
            alertDefaultCouldntAddChangesEmtpyField_DEF =
                    language.alertDefaultCouldntAddChangesEmtpyField_1;
            alertDefaultCouldntAddRecord_DEF = language.
                                               alertDefaultCouldntAddRecord_1;
            alsertDefaultChangesSave_DEF = language.alertDefaultChangesSave_1;
            mgsDefaultContactListIsEmpty_DEF = language.
                                               mgsDefaultContactListIsEmpty_1;
            alertDefaultCountryCodeError_DEF = language.
                                               alertDefaultCountryCodeError_1;
            settingsDefaultCountryCode_DEF = language.
                                             settingsDefaultCountryCode_1;
            genDefaultDelete_DEF = language.genDefaultDelete_1;
            genDefaultDeleteAll_DEF = language.genDefaultDeleteAll_1;
            dialledCallsDefault_DEF = language.dialledCallsDefault_1;
            callForwardDefaultDontDisturb_DEF =
                    language.callForwardDefaultDontDisturb_1;
            genDefaultEdit_DEF = language.genDefaultEdit_1;
            settingsDefaultEditPBXAccess_DEF = language.
                                               settingsDefaultEditPBXAccess_1;
            absentDefaultEditPresence_DEF = language.
                                            absentDefaultEditPresence_1;
            voiceMailDefaultEditVoicemail_DEF =
                    language.voiceMailDefaultEditVoicemail_1;
            enterDefaultEngerCharacter_DEF = language.
                                             enterDefaultEngerCharacter_1;
            enterDefaultEnterExtension_DEF = language.
                                             enterDefaultEnterExtension_1;
            enterDefaultEnterGroupNumber_DEF = language.
                                               enterDefaultEnterGroupNumber_1;
            enterDefaultEnterHHMM_DEF = language.enterDefaultEnterHHMM_1;
            enterDefaultEnterNumber_DEF = language.enterDefaultEnterNumber_1;
            alertDefaultErrorChangeTo_DEF = language.
                                            alertDefaultErrorChangeTo_1;
            alertDefaultError_DEF = language.alertDefaultError_1;
            enterDefaultEnterMMDD_DEF = language.enterDefaultEnterMMDD_1;
            exitDefaultExitTheProgramYesOrNo_DEF =
                    language.exitDefaultExitTheProgramYesOrNo_1;
            genDefaultExit_DEF = language.genDefaultExit_1;
            settingsDefaultExtension_DEF = language.settingsDefaultExtension_1;
            callExtensionDefaultExtensionWasAdded_DEF =
                    language.callExtensionDefaultExtensionWasAdded_1;
            callForwardDefaultExternCalls_DEF =
                    language.callForwardDefaultExternCalls_1;
            absentDefaultGoneHome_DEF = language.absentDefaultGoneHome_1;
            groupsDefaultGroups_DEF = language.groupsDefaultGroups_1;
            settingsDefaultHelp_DEF = language.settingsDefaultHelp_1;
            absentDefaultInAMeeting_DEF = language.absentDefaultInAMeeting_1;
            alertDefaultInfo_DEF = language.alertDefaultInfo_1;
            alertDefaultInstedOf_DEF = language.alertDefaultInstedOf_1;
            callForwardDefaultInternCalls_DEF =
                    language.callForwardDefaultInternCalls_1;
            settingsDefaultLanguage_DEF = language.settingsDefaultLanguage_1;
            settingsDefaultLineAccess_DEF = language.
                                            settingsDefaultLineAccess_1;
            groupsDefaultLoginAllGroups_DEF = language.
                                              groupsDefaultLoginAllGroups_1;
            groupsDefaultLoginSpecificGroup_DEF =
                    language.groupsDefaultLoginSpecificGroup_1;
            groupsDefaultLogoutAllGroups_DEF = language.
                                               groupsDefaultLogoutAllGroups_1;
            groupsDefaultLogoutSpecificGroup_DEF =
                    language.groupsDefaultLogoutSpecificGroup_1;
            alertDefaultMaxSize_DEF = language.alertDefaultMaxSize_1;
            genDefaultMinimise_DEF = language.genDefaultMinimise_1;
            callExtensionDefaultName_DEF = language.callExtensionDefaultName_1;
            exitDefaultNo_DEF = language.exitDefaultNo_1;
            callForwardDefaultNoAnswer_DEF = language.
                                             callForwardDefaultNoAnswer_1;
            settingsDefaultOptions_DEF = language.settingsDefaultOptions_1;
            absentDefaultOutUntil_DEF = language.absentDefaultOutUntil_1;

            settingsDefaultPINcode_DEF = language.settingsDefaultPINcode_1;
            settingsDefaultPreEditCode_DEF = language.
                                             settingsDefaultPreEditCode_1;
            callForwardDefaultRemove_DEF = language.callForwardDefaultRemove_1;
            absentDefaultRemovePresence_DEF = language.
                                              absentDefaultRemovePresence_1;
            exitDefaultRestartProgram_DEF = language.
                                            exitDefaultRestartProgram_1;
            alertDefaultSaveChanges_DEF = language.alertDefaultSaveChanges_1;
            genDefaultSave_DEF = language.genDefaultSave_1;
            settingsDefaultSelectCountryCode_DEF =
                    language.settingsDefaultSelectCountryCode_1;
            genDefaultSelect_DEF = language.genDefaultSelect_1;
            genDefaultSend_DEF = language.genDefaultSend_1;
            absentDefaultSetPresence_DEF = language.absentDefaultSetPresence_1;
            settingsDefaultSettings_DEF = language.settingsDefaultSettings_1;
            callExtensionDefaultSurname_DEF = language.
                                              callExtensionDefaultSurname_1;
            settingsDefaultSwitchboardNumber_DEF =
                    language.settingsDefaultSwitchboardNumber_1;

            absentDefaultSystemAttOne_DEF = language.
                                            absentDefaultSystemAttOne_1;
            absentDeafaultSystemAttTwo_DEF = language.
                                             absentDeafaultSystemAttTwo_1;
            absentDefaultPersonalAtt_DEF = language.absentDefaultPersonalAtt_1;

            absentDeafaultWillReturnSoon_DEF = language.
                                               absentDeafaultWillReturnSoon_1;
            alertDefaultWrongInputTryAgain_DEF =
                    language.alertDefaultWrongInputTryAgain_1;
            voiceMailDefaultVoiceMail_DEF = language.
                                            voiceMailDefaultVoiceMail_1;
            genDefaultYes_DEF = language.genDefaultYes_1;

            accessPBXDefault_DEF = language.accessPBXDefault_1;
            autoAccessDefault_DEF = language.autoAccessDefault_1;
            accessViaPINCodeDefault_DEF = language.accessViaPINCodeDefault_1;
            dialDefault_DEF = language.dialDefault_1;

            alertExitMEXOnMessage_DEF = language.alertExitMEXOnMessage_1;
            alertMessageMEXOn_DEF = language.alertMessageMEXOn_1;
            alertMessageMEXOff_DEF = language.alertMessageMEXOff_1;
            alertMessageMexServerInfo_DEF = language.
                                            alertMessageMexServerInfo_1;
            alertMessageMexAlreadyON_DEF = language.alertMessageMexAlreadyON_1;
            alertMessageMexAlreadyOFF_DEF = language.
                                            alertMessageMexAlreadyOFF_1;
            mainListAttributMexOn_DEF = language.mainListAttributMexOn_1;
            mainListAttributMexOff_DEF = language.mainListAttributMexOff_1;
            operatorVoicemail_DEF = language.operatorVoicemail_1;

            AlertMessageExitText_DEF = language.AlertMessageExitText_1;

            absentTimeOfReturn_DEF = language.absentTimeOfReturn_1;
            absentDateOfReturn_DEF = language.absentDateOfReturn_1;
            absentLunch_DEF = language.absentLunch_1;
            absentMeeting_DEF = language.absentMeeting_1;
            absentVacation_DEF = language.absentVacation_1;
            absentIllness_DEF = language.absentIllness_1;

            callForwardTransfer_DEF = language.callForwardTransfer_1;
            callForwardPermForward_DEF = language.callForwardPermForward_1;
            callForwardInterForward_DEF = language.callForwardInterForward_1;
            callForwardExternForward_DEF = language.callForwardExternForward_1;
            callForwardCancelExtern_DEF = language.callForwardCancelExtern_1;
            callForwardCancelPermIntern_DEF = language.
                                              callForwardCancelPermIntern_1;

            textYourNumber_DEF = language.textYourNumber_1;
            textNewNumber_DEF = language.textNewNumber_1;

            voiceMailActivate_DEF = language.voiceMailActivate_1;
            voiceMailDeActivate_DEF = language.voiceMailDeActivate_1;
            voiceMailListen_DEF = language.voiceMailListen_1;

            version_DEF = language.version_1;
            system_DEF = language.system_1;

            programExitON_DEF = language.programExitON_1;
            programExitOFF_DEF = language.programExitOFF_1;

            transferBack_DEF = language.transferBack_1;

        }

        // --- Övriga språk beroende på vilket nummer som är satt i DB.

        /* Danish, Dutch, Finnish, French, German,
           Norwegian, Italian, Spanish, Swedish */
        if (this.lang_PBX.equals("0") || this.lang_PBX.equals("1")
            || this.lang_PBX.equals("3") || this.lang_PBX.equals("4")
            || this.lang_PBX.equals("5") || this.lang_PBX.equals("6")
            || this.lang_PBX.equals("7") || this.lang_PBX.equals("8")
            || this.lang_PBX.equals("9")) {

            settingsDefaultAbout_DEF = language.settingsDefaultAbout_2;
            SettingsDefaultAccessPINcode_DEF = language.
                                               SettingsDefaultAccessPINcode_2;
            extensionDefaultAddNew_DEF = language.extensionDefaultAddNew_2;
            callForwardDefaultAllCalls_DEF = language.
                                             callForwardDefaultAllCalls_2;
            absentDefaultAtExt_DEF = language.absentDefaultAtExt_2;
            settingsDefaultAutoAccess_DEF = language.
                                            settingsDefaultAutoAccess_2;
            genDefaultBack_DEF = language.genDefaultBack_2;
            absentDefaultBackAt_DEF = language.absentDefaultBackAt_2;
            callForwardDefaultBusy_DEF = language.callForwardDefaultBusy_2;
            callForwardDefaultBusyNoAnswer_DEF =
                    language.callForwardDefaultBusyNoAnswer_2;
            callDefaultCall_DEF = language.callDefaultCall_2;
            extensionDefaultCall_DEF = language.extensionDefaultCall_2;
            callForwardDefault_DEF = language.callForwardDefault_2;
            msgDefaultCallistIsEmpty_DEF = language.msgDefaultCallistIsEmpty_2;
            genDefaultCancel_DEF = language.genDefaultCancel_2;
            alertDefaultCantAddAnymoreExt_DEF =
                    language.alertDefaultCantAddAnymoreExt_2;
            alertDefaultCouldntAddChangesEmtpyField_DEF =
                    language.alertDefaultCouldntAddChangesEmtpyField_2;
            alertDefaultCouldntAddRecord_DEF = language.
                                               alertDefaultCouldntAddRecord_2;
            alsertDefaultChangesSave_DEF = language.alertDefaultChangesSave_2;
            mgsDefaultContactListIsEmpty_DEF = language.
                                               mgsDefaultContactListIsEmpty_2;
            alertDefaultCountryCodeError_DEF = language.
                                               alertDefaultCountryCodeError_2;
            settingsDefaultCountryCode_DEF = language.
                                             settingsDefaultCountryCode_2;
            genDefaultDelete_DEF = language.genDefaultDelete_2;
            genDefaultDeleteAll_DEF = language.genDefaultDeleteAll_2;
            dialledCallsDefault_DEF = language.dialledCallsDefault_2;
            callForwardDefaultDontDisturb_DEF =
                    language.callForwardDefaultDontDisturb_2;
            genDefaultEdit_DEF = language.genDefaultEdit_2;
            settingsDefaultEditPBXAccess_DEF = language.
                                               settingsDefaultEditPBXAccess_2;
            absentDefaultEditPresence_DEF = language.
                                            absentDefaultEditPresence_2;
            voiceMailDefaultEditVoicemail_DEF =
                    language.voiceMailDefaultEditVoicemail_2;
            enterDefaultEngerCharacter_DEF = language.
                                             enterDefaultEngerCharacter_2;
            enterDefaultEnterExtension_DEF = language.
                                             enterDefaultEnterExtension_2;
            enterDefaultEnterGroupNumber_DEF = language.
                                               enterDefaultEnterGroupNumber_2;
            enterDefaultEnterHHMM_DEF = language.enterDefaultEnterHHMM_2;
            enterDefaultEnterNumber_DEF = language.enterDefaultEnterNumber_2;
            alertDefaultErrorChangeTo_DEF = language.
                                            alertDefaultErrorChangeTo_2;
            alertDefaultError_DEF = language.alertDefaultError_2;
            enterDefaultEnterMMDD_DEF = language.enterDefaultEnterMMDD_2;
            exitDefaultExitTheProgramYesOrNo_DEF =
                    language.exitDefaultExitTheProgramYesOrNo_2;
            genDefaultExit_DEF = language.genDefaultExit_2;
            settingsDefaultExtension_DEF = language.settingsDefaultExtension_2;
            callExtensionDefaultExtensionWasAdded_DEF =
                    language.callExtensionDefaultExtensionWasAdded_2;
            callForwardDefaultExternCalls_DEF =
                    language.callForwardDefaultExternCalls_2;
            absentDefaultGoneHome_DEF = language.absentDefaultGoneHome_2;
            groupsDefaultGroups_DEF = language.groupsDefaultGroups_2;
            settingsDefaultHelp_DEF = language.settingsDefaultHelp_2;
            absentDefaultInAMeeting_DEF = language.absentDefaultInAMeeting_2;
            alertDefaultInfo_DEF = language.alertDefaultInfo_2;
            alertDefaultInstedOf_DEF = language.alertDefaultInstedOf_2;
            callForwardDefaultInternCalls_DEF =
                    language.callForwardDefaultInternCalls_2;
            settingsDefaultLanguage_DEF = language.settingsDefaultLanguage_2;
            settingsDefaultLineAccess_DEF = language.
                                            settingsDefaultLineAccess_2;
            groupsDefaultLoginAllGroups_DEF = language.
                                              groupsDefaultLoginAllGroups_2;
            groupsDefaultLoginSpecificGroup_DEF =
                    language.groupsDefaultLoginSpecificGroup_2;
            groupsDefaultLogoutAllGroups_DEF = language.
                                               groupsDefaultLogoutAllGroups_2;
            groupsDefaultLogoutSpecificGroup_DEF =
                    language.groupsDefaultLogoutSpecificGroup_2;
            alertDefaultMaxSize_DEF = language.alertDefaultMaxSize_2;
            genDefaultMinimise_DEF = language.genDefaultMinimise_2;
            callExtensionDefaultName_DEF = language.callExtensionDefaultName_2;
            exitDefaultNo_DEF = language.exitDefaultNo_2;
            callForwardDefaultNoAnswer_DEF = language.
                                             callForwardDefaultNoAnswer_2;
            settingsDefaultOptions_DEF = language.settingsDefaultOptions_2;
            absentDefaultOutUntil_DEF = language.absentDefaultOutUntil_2;
            settingsDefaultPINcode_DEF = language.settingsDefaultPINcode_2;
            settingsDefaultPreEditCode_DEF = language.
                                             settingsDefaultPreEditCode_2;
            callForwardDefaultRemove_DEF = language.callForwardDefaultRemove_2;
            absentDefaultRemovePresence_DEF = language.
                                              absentDefaultRemovePresence_2;
            exitDefaultRestartProgram_DEF = language.
                                            exitDefaultRestartProgram_2;
            alertDefaultSaveChanges_DEF = language.alertDefaultSaveChanges_2;
            genDefaultSave_DEF = language.genDefaultSave_2;
            settingsDefaultSelectCountryCode_DEF =
                    language.settingsDefaultSelectCountryCode_2;
            genDefaultSelect_DEF = language.genDefaultSelect_2;
            genDefaultSend_DEF = language.genDefaultSend_2;
            absentDefaultSetPresence_DEF = language.absentDefaultSetPresence_2;
            settingsDefaultSettings_DEF = language.settingsDefaultSettings_2;
            callExtensionDefaultSurname_DEF = language.
                                              callExtensionDefaultSurname_2;
            settingsDefaultSwitchboardNumber_DEF =
                    language.settingsDefaultSwitchboardNumber_2;

            absentDefaultSystemAttOne_DEF = language.
                                            absentDefaultSystemAttOne_2;
            absentDeafaultSystemAttTwo_DEF = language.
                                             absentDeafaultSystemAttTwo_2;
            absentDefaultPersonalAtt_DEF = language.absentDefaultPersonalAtt_2;

            absentDeafaultWillReturnSoon_DEF = language.
                                               absentDeafaultWillReturnSoon_2;
            alertDefaultWrongInputTryAgain_DEF =
                    language.alertDefaultWrongInputTryAgain_2;
            voiceMailDefaultVoiceMail_DEF = language.
                                            voiceMailDefaultVoiceMail_2;
            genDefaultYes_DEF = language.genDefaultYes_2;

            accessPBXDefault_DEF = language.accessPBXDefault_2;
            autoAccessDefault_DEF = language.autoAccessDefault_2;
            accessViaPINCodeDefault_DEF = language.accessViaPINCodeDefault_2;
            dialDefault_DEF = language.dialDefault_2;

            alertExitMEXOnMessage_DEF = language.alertExitMEXOnMessage_2;
            alertMessageMEXOn_DEF = language.alertMessageMEXOn_2;
            alertMessageMEXOff_DEF = language.alertMessageMEXOff_2;
            alertMessageMexServerInfo_DEF = language.
                                            alertMessageMexServerInfo_2;
            alertMessageMexAlreadyON_DEF = language.alertMessageMexAlreadyON_2;
            alertMessageMexAlreadyOFF_DEF = language.
                                            alertMessageMexAlreadyOFF_2;
            mainListAttributMexOn_DEF = language.mainListAttributMexOn_2;
            mainListAttributMexOff_DEF = language.mainListAttributMexOff_2;
            operatorVoicemail_DEF = language.operatorVoicemail_2;

            AlertMessageExitText_DEF = language.AlertMessageExitText_2;

            absentTimeOfReturn_DEF = language.absentTimeOfReturn_2;
            absentDateOfReturn_DEF = language.absentDateOfReturn_2;
            absentLunch_DEF = language.absentLunch_2;
            absentMeeting_DEF = language.absentMeeting_2;
            absentVacation_DEF = language.absentVacation_2;
            absentIllness_DEF = language.absentIllness_2;

            callForwardTransfer_DEF = language.callForwardTransfer_2;
            callForwardPermForward_DEF = language.callForwardPermForward_2;
            callForwardInterForward_DEF = language.callForwardInterForward_2;
            callForwardExternForward_DEF = language.callForwardExternForward_2;
            callForwardCancelExtern_DEF = language.callForwardCancelExtern_2;
            callForwardCancelPermIntern_DEF = language.
                                              callForwardCancelPermIntern_2;

            textYourNumber_DEF = language.textYourNumber_2;
            textNewNumber_DEF = language.textNewNumber_2;

            voiceMailActivate_DEF = language.voiceMailActivate_2;
            voiceMailDeActivate_DEF = language.voiceMailDeActivate_2;
            voiceMailListen_DEF = language.voiceMailListen_2;

            version_DEF = language.version_2;
            system_DEF = language.system_2;

            programExitON_DEF = language.programExitON_2;
            programExitOFF_DEF = language.programExitOFF_2;

            transferBack_DEF = language.transferBack_2;

        }

        /* ================== DEBUG-VY ================================= */

        // Form
        logDataForm = new Form("DeBug - Login");
        logDataTextField = new TextField(settingsDefaultPINcode_DEF, "", 4,
                                         TextField.NUMERIC);

        logDataLogInCommand = new Command("Login", Command.OK, 1);
        logDataCancelLogInCommand = new Command(genDefaultCancel_DEF,
                                                Command.CANCEL, 2);

        logDataForm.addCommand(logDataLogInCommand);
        logDataForm.addCommand(logDataCancelLogInCommand);
        logDataForm.setCommandListener(this);

        // Debug list
        debug_List = new List("Debug - On/Off", Choice.IMPLICIT);

        try {
            Image image1debug = Image.createImage("/prg_icon/on24.png");
            Image image2debug = Image.createImage("/prg_icon/off24.png");
            Image image3debug = Image.createImage("/prg_icon/bortrest24.png");

            debug_List.append("Debug On", image1debug);
            debug_List.append("Debug Off", image2debug);
            debug_List.append("Send Log", image3debug);

        } catch (IOException ex4) {
        }
        //alertLogOutDebug
        debugListLogOutCommand = new Command("Logout", Command.BACK, 2);

        debug_List.addCommand(debugListLogOutCommand);
        debug_List.setCommandListener(this);

        // linePrefix_List linePrefixBackCommand

        linePrefix_List = new List(settingsDefaultLineAccess_DEF,
                                   Choice.IMPLICIT);

        linePrefix_List.append(genDefaultYes_DEF, null);
        linePrefix_List.append(exitDefaultNo_DEF, null);

        linePrefixBackCommand = new Command(genDefaultBack_DEF, Command.BACK, 2);

        linePrefix_List.addCommand(linePrefixBackCommand);
        linePrefix_List.setCommandListener(this);

        /* ================== KOPPLA SAMTAL ============================ */

        connectPhoneForm = new Form(callForwardTransfer_DEF);
        connectTextField = new TextField(enterDefaultEnterNumber_DEF, "", 5,
                                         TextField.PHONENUMBER);

        connectSendCommand = new Command(genDefaultSend_DEF, Command.OK, 1);
        connectResumeCommand = new Command(transferBack_DEF, Command.OK, 2);
        connectBackCommand = new Command(genDefaultBack_DEF, Command.BACK, 3);
        // connectEditRenameCommand = new Command(genDefaultEdit_DEF, Command.OK,3);
        connectPhoneForm.addCommand(connectSendCommand);
        connectPhoneForm.addCommand(connectResumeCommand);
        connectPhoneForm.addCommand(connectBackCommand);
        // connectPhoneForm.addCommand(connectEditRenameCommand);
        connectPhoneForm.setCommandListener(this);
        // connectPhoneForm.setItemStateListener(this);

        // --- Edit connectEditForm

        connectEditForm = new Form(callForwardTransfer_DEF);

        connectEditNameTextField = new TextField(callExtensionDefaultName_DEF,
                                                 "", 30, TextField.SENSITIVE);
        connectEditExtensionTextField = new TextField(
                settingsDefaultExtension_DEF, "", 5, TextField.PHONENUMBER);

        connectEditSaveCommand = new Command(genDefaultSave_DEF, Command.OK, 1);
        connectEditBackCommand = new Command(genDefaultBack_DEF, Command.BACK,
                                             2);
        connectEditCancelCommand = new Command(genDefaultCancel_DEF,
                                               Command.CANCEL, 3);

        connectEditForm.addCommand(connectEditSaveCommand);
        connectEditForm.addCommand(connectEditBackCommand);
        connectEditForm.addCommand(connectEditCancelCommand);
        connectEditForm.setCommandListener(this);

        // --- Rename connectRenameForm

        connectRenameForm = new Form(callForwardTransfer_DEF);

        connectRenameBackCommand = new Command(genDefaultBack_DEF, Command.BACK,
                                               2);

        connectRenameForm.addCommand(connectRenameBackCommand);
        connectRenameForm.setCommandListener(this);
        connectRenameForm.setItemStateListener(this);

        // --- Edit connectRenameEditForm

        connectRenameEditForm = new Form(callForwardTransfer_DEF);

        connectEditRenameNameTextField = new TextField(
                callExtensionDefaultName_DEF, "", 30, TextField.SENSITIVE);
        connectEditRenameExtensionTextField = new TextField(
                settingsDefaultExtension_DEF, "", 5, TextField.PHONENUMBER);

        connectEditRenameSaveCommand = new Command(genDefaultSave_DEF,
                Command.OK, 1);
        connectEditRenameBackCommand = new Command(genDefaultBack_DEF,
                Command.BACK, 2);
        connectEditRenameCancelCommand = new Command(genDefaultCancel_DEF,
                Command.CANCEL, 3);

        connectRenameEditForm.addCommand(connectEditRenameSaveCommand);
        connectRenameEditForm.addCommand(connectEditRenameBackCommand);
        connectRenameEditForm.addCommand(connectEditRenameCancelCommand);
        connectRenameEditForm.setCommandListener(this);

        /* ================== HÄNVISNING ================================ */

        //--- AbsentList, lista för hänvisning i prg.

        //---- Finns på anknytning

        atExtForm = new Form(absentDefaultAtExt_DEF);
        atExtTextField = new TextField(enterDefaultEnterExtension_DEF, "", 4,
                                       TextField.NUMERIC);

        atExtSendCommand = new Command(genDefaultSend_DEF, Command.OK, 1);
        atExtBackCommand = new Command(genDefaultBack_DEF, Command.BACK, 2);
        atExtForm.addCommand(atExtSendCommand);
        atExtForm.addCommand(atExtBackCommand);
        atExtForm.setCommandListener(this);

        //--- Åter Klockan

        backAtForm = new Form(absentDefaultBackAt_DEF);
        backAtTextField = new TextField(enterDefaultEnterHHMM_DEF, "", 4,
                                        TextField.NUMERIC);

        backAtSendCommand = new Command(genDefaultSend_DEF, Command.OK, 1);
        backAtBackCommand = new Command(genDefaultBack_DEF, Command.BACK, 2);
        backAtForm.addCommand(backAtSendCommand);
        backAtForm.addCommand(backAtBackCommand);
        backAtForm.setCommandListener(this);

        //--- Ute

        outForm = new Form(absentDefaultOutUntil_DEF);
        outTextField = new TextField(enterDefaultEnterMMDD_DEF, "", 4,
                                     TextField.NUMERIC);

        outSendCommand = new Command(genDefaultSend_DEF, Command.OK, 1);
        outBackCommand = new Command(genDefaultBack_DEF, Command.BACK, 2);
        outForm.addCommand(outSendCommand);
        outForm.addCommand(outBackCommand);
        outForm.setCommandListener(this);

        /* =================== GRUPPER ================================= */

        //--- GroupList, lista för att välja svarsgrupper i PBX'en.

        groupList = new List("", Choice.IMPLICIT); // skapar en lista
        groupList.setTitle(groupsDefaultGroups_DEF);

        groupBackCommand = new Command(genDefaultBack_DEF, Command.BACK, 1);

        try {

            Image imageGroup = Image.createImage("/prg_icon/konf24.png");

            groupList.append(groupsDefaultLoginAllGroups_DEF, imageGroup);
            groupList.append(groupsDefaultLogoutAllGroups_DEF, imageGroup);
            groupList.append(groupsDefaultLoginSpecificGroup_DEF, imageGroup);
            groupList.append(groupsDefaultLogoutSpecificGroup_DEF, imageGroup);

            groupList.addCommand(groupBackCommand);
            groupList.setCommandListener(this);

        } catch (IOException ex) {
            System.out.println("Unable to Find or Read .png file");
        }

        //--- logoutGroup

        logoffGroupForm = new Form(groupsDefaultLogoutSpecificGroup_DEF);
        logoffGroupTextField = new TextField(enterDefaultEnterNumber_DEF, "", 5,
                                             TextField.NUMERIC);

        logoffGroupSendCommand = new Command(genDefaultSend_DEF, Command.OK, 1);
        logoffGroupBackCommand = new Command(genDefaultBack_DEF, Command.BACK,
                                             2);

        logoffGroupForm.addCommand(logoffGroupSendCommand);
        logoffGroupForm.addCommand(logoffGroupBackCommand);
        logoffGroupForm.setCommandListener(this);

        //--- loginGroup

        loginGroupForm = new Form(groupsDefaultLoginSpecificGroup_DEF);
        loginGroupTextField = new TextField(enterDefaultEnterNumber_DEF, "", 5,
                                            TextField.NUMERIC);

        loginGroupSendCommand = new Command(genDefaultSend_DEF, Command.OK, 1);
        loginGroupBackCommand = new Command(genDefaultBack_DEF, Command.BACK, 2);

        loginGroupForm.addCommand(loginGroupSendCommand);
        loginGroupForm.addCommand(loginGroupBackCommand);
        loginGroupForm.setCommandListener(this);

        /* =================== VIDAREKOPPLA ============================== */

        // --- Alla samtal
        allCallForwardForm = new Form("");
        allCallForwardTextField = new TextField(enterDefaultEnterNumber_DEF, "",
                                                32, TextField.PHONENUMBER);

        allCallForwardSendCommand = new Command(genDefaultSend_DEF, Command.OK,
                                                1);
        allCallForwardBackCommand = new Command(genDefaultBack_DEF,
                                                Command.BACK, 2);

        allCallForwardForm.addCommand(allCallForwardSendCommand);
        allCallForwardForm.addCommand(allCallForwardBackCommand);
        allCallForwardForm.setCommandListener(this);

        // --- Externa samtal
        externCallForwardForm = new Form("");
        externCallForwardTextField = new TextField(enterDefaultEnterNumber_DEF,
                "", 32, TextField.PHONENUMBER);

        externCallForwardSendCommand = new Command(genDefaultSend_DEF,
                Command.OK, 1);
        externCallForwardBackCommand = new Command(genDefaultBack_DEF,
                Command.BACK, 2);

        externCallForwardForm.addCommand(externCallForwardSendCommand);
        externCallForwardForm.addCommand(externCallForwardBackCommand);
        externCallForwardForm.setCommandListener(this);

        // --- Interna samtal
        internCallForwardForm = new Form("");
        internCallForwardTextField = new TextField(enterDefaultEnterNumber_DEF,
                "", 32, TextField.PHONENUMBER);

        internCallForwardSendCommand = new Command(genDefaultSend_DEF,
                Command.OK, 1);
        internCallForwardBackCommand = new Command(genDefaultBack_DEF,
                Command.BACK, 2);

        internCallForwardForm.addCommand(internCallForwardSendCommand);
        internCallForwardForm.addCommand(internCallForwardBackCommand);
        internCallForwardForm.setCommandListener(this);

        /* =================== RÖSTBREVLÅDOR ============================= */

        //---- Operatörs Röstbrevlåda, lägger in nummer till voicemail.

        voiceOperatorMessageForm = new Form(voiceMailDefaultVoiceMail_DEF);

        voiceOperatorMessageTextField = new TextField(
                enterDefaultEnterNumber_DEF + ":", "", 32,
                TextField.PHONENUMBER);

        voiceOperatorMessageSaveCommand = new Command(genDefaultSave_DEF,
                Command.OK, 1);
        voiceOperatorMessageCancelCommand = new Command(genDefaultCancel_DEF,
                Command.CANCEL, 2);
        voiceOperatorMessageBackCommand = new Command(genDefaultBack_DEF,
                Command.BACK, 3);

        voiceOperatorMessageForm.addCommand(voiceOperatorMessageBackCommand);
        voiceOperatorMessageForm.addCommand(voiceOperatorMessageCancelCommand);
        voiceOperatorMessageForm.addCommand(voiceOperatorMessageSaveCommand);
        voiceOperatorMessageForm.setCommandListener(this);

        //--- Röstbrevlåda PBX, vxl'ens egna voicemail.

        voiceEditForm_PBX = new Form(voiceMailDefaultEditVoicemail_DEF);
        voiceMailPBXTextField_PBX = new TextField(enterDefaultEnterNumber_DEF,
                                                  "", 4, TextField.NUMERIC);

        voiceEditSaveCommand_PBX = new Command(genDefaultSave_DEF, Command.OK,
                                               1);
        voiceEditCancelCommand_PBX = new Command(genDefaultCancel_DEF,
                                                 Command.CANCEL, 2);
        voiceEditBackcommand_PBX = new Command(genDefaultBack_DEF, Command.BACK,
                                               3);

        voiceEditForm_PBX.addCommand(voiceEditSaveCommand_PBX);
        voiceEditForm_PBX.addCommand(voiceEditBackcommand_PBX);
        voiceEditForm_PBX.addCommand(voiceEditCancelCommand_PBX);
        voiceEditForm_PBX.setCommandListener(this);

        // --- Röstbrevlåda PBX, voiceMailPBXList

        voiceMailPBXList = new List(voiceMailDefaultVoiceMail_DEF,
                                    Choice.IMPLICIT);

        try {
            Image voiceImage = Image.createImage("/prg_icon/rostbrevlada24.png");

            voiceMailPBXList.append(voiceMailActivate_DEF, voiceImage);
            voiceMailPBXList.append(voiceMailDeActivate_DEF, voiceImage);
            voiceMailPBXList.append(voiceMailListen_DEF, voiceImage);

        } catch (IOException ex4) {
        }

        voiceMailPBXListBackCommand = new Command(genDefaultBack_DEF,
                                                  Command.BACK, 2);

        voiceMailPBXList.addCommand(voiceMailPBXListBackCommand);
        voiceMailPBXList.setCommandListener(this);

        /* ================== REDIGERA/EDIT ============================ */

        //--- pbx_List, editerar olika 'settings' i prg. som vxlnr osv...

        pbx_List = new List(settingsDefaultEditPBXAccess_DEF, Choice.IMPLICIT);

        pbx_List.append(accessPBXDefault_DEF, null);
        pbx_List.append(operatorVoicemail_DEF, null);
        pbx_List.append(voiceMailDefaultEditVoicemail_DEF, null);
        pbx_List.append(absentDefaultEditPresence_DEF, null);
        pbx_List.append(settingsDefaultPreEditCode_DEF, null);
        pbx_List.append(settingsDefaultLanguage_DEF, null);
        pbx_List.append(system_DEF, null);

        pbx_ListCancelCommand = new Command(genDefaultBack_DEF, Command.BACK, 2);

        pbx_List.addCommand(pbx_ListCancelCommand);
        pbx_List.setCommandListener(this);

        //-------- pbx_List_type, editerar olika settings för PBX-typer

        pbx_List_type = new List(settingsDefaultEditPBXAccess_DEF,
                                 Choice.IMPLICIT);
        pbx_List_type.append(autoAccessDefault_DEF, null);
        pbx_List_type.append(accessViaPINCodeDefault_DEF, null);

        pbx_List_typeBackCommand = new Command(genDefaultBack_DEF, Command.BACK,
                                               1);
        pbx_List_typeCancelCommand = new Command(genDefaultCancel_DEF,
                                                 Command.CANCEL, 2);

        pbx_List_type.addCommand(pbx_List_typeBackCommand);
        pbx_List_type.addCommand(pbx_List_typeCancelCommand);
        pbx_List_type.setCommandListener(this);

        //--- language_List, editerar att byta språk i programmet.

        language_List = new List(settingsDefaultLanguage_DEF, Choice.IMPLICIT);
        try {
            String iconPath = "/prg_icon/" + rms.getIconNumber() + ".png";

            System.out.println("ICONPATH >> " + iconPath);

            Image imageEng = Image.createImage("/prg_icon/2.png");
            Image imageIcon = Image.createImage(iconPath);

            language_List.append("English", imageEng);

            // --- om eng_lang är satt till '2' så visa aldrig annat språk.
            if (!this.eng_lang.equals("2")) {

                language_List.append(this.countryName_PBX, imageIcon);

            }

            languageListBackCommand = new Command(genDefaultBack_DEF,
                                                  Command.BACK, 2);

            language_List.addCommand(languageListBackCommand);
            language_List.setCommandListener(this);

        } catch (IOException ex) {
            System.out.println("Unable to Find or Read .png file");
        }

        //------------- PRE-EDIT-FORM ------------------------------------------

        preEditForm = new Form(settingsDefaultPreEditCode_DEF);
        preEditTextField = new TextField(enterDefaultEngerCharacter_DEF, "", 2,
                                         TextField.PHONENUMBER);

        preEditSaveCommand = new Command(genDefaultSave_DEF, Command.OK, 1);
        preEditBackCommand = new Command(genDefaultBack_DEF, Command.BACK, 2);
        preEditCancelCommand = new Command(genDefaultCancel_DEF, Command.CANCEL,
                                           3);

        preEditForm.addCommand(preEditSaveCommand);
        preEditForm.addCommand(preEditBackCommand);
        preEditForm.addCommand(preEditCancelCommand);
        preEditForm.setCommandListener(this);

        // --- pinCodeSettingsForm, editerar PIN-Code på PBX'en.

        pinCodeSettingsForm = new Form(accessViaPINCodeDefault_DEF);

        pinCodeLineAccessTextField = new TextField(
                settingsDefaultLineAccess_DEF, "", 32, TextField.NUMERIC);

        pinCodeSwitchBoardNumberTextField = new TextField(
                settingsDefaultSwitchboardNumber_DEF, "", 32,
                TextField.PHONENUMBER);

        pinCodeExtensionNumberTextField = new TextField(
                settingsDefaultExtension_DEF, "", 32, TextField.PHONENUMBER);

        pinCodePinCodeNumberTextField = new TextField(
                settingsDefaultPINcode_DEF, "", 32, TextField.NUMERIC);

        pinCodeSaveCommand = new Command(genDefaultSave_DEF, Command.OK, 1);
        pinCodeBackCommand = new Command(genDefaultBack_DEF, Command.BACK, 2);
        pinCodeCancelCommand = new Command(genDefaultCancel_DEF, Command.CANCEL,
                                           3);

        pinCodeSettingsForm.addCommand(pinCodeSaveCommand);
        pinCodeSettingsForm.addCommand(pinCodeBackCommand);
        pinCodeSettingsForm.addCommand(pinCodeCancelCommand);
        pinCodeSettingsForm.setCommandListener(this);

        //--- AutoAccessSettingsForm, editerar autologin på PBX'en.

        AutoAccessSettingsForm = new Form(autoAccessDefault_DEF);

        AutoAccessLineAccessTextField = new TextField(
                settingsDefaultLineAccess_DEF, "", 32,
                TextField.NUMERIC);

        AutoAccessSwitchBoardTextField = new TextField(
                settingsDefaultSwitchboardNumber_DEF, "",
                32,
                TextField.PHONENUMBER);

        AutoAccessBackCommand = new Command(genDefaultBack_DEF,
                                            Command.BACK, 2);
        AutoAccessCancelCommand = new Command(genDefaultCancel_DEF,
                                              Command.CANCEL,
                                              3);
        AutoAccessSaveCommand = new Command(genDefaultSave_DEF, Command.OK, 1);

        AutoAccessSettingsForm.addCommand(AutoAccessBackCommand);
        AutoAccessSettingsForm.addCommand(AutoAccessCancelCommand);
        AutoAccessSettingsForm.addCommand(AutoAccessSaveCommand);
        AutoAccessSettingsForm.setCommandListener(this);

        //--- AutoAccessSettingsNOPrefixForm, editerar autologin på PBX'en.

        AutoAccessSettingsNOPrefixForm = new Form(autoAccessDefault_DEF);

        AutoAccessNOPrefixSwitchBoardTextField = new TextField(
                settingsDefaultSwitchboardNumber_DEF, "",
                32,
                TextField.PHONENUMBER);

        AutoAccessBackNOPrefixCommand = new Command(genDefaultBack_DEF,
                Command.BACK, 2);
        AutoAccessCancelNOPrefixCommand = new Command(genDefaultCancel_DEF,
                Command.CANCEL,
                3);
        AutoAccessSaveNOPrefixCommand = new Command(genDefaultSave_DEF,
                Command.OK, 1);

        AutoAccessSettingsNOPrefixForm.addCommand(AutoAccessBackNOPrefixCommand);
        AutoAccessSettingsNOPrefixForm.addCommand(
                AutoAccessCancelNOPrefixCommand);
        AutoAccessSettingsNOPrefixForm.addCommand(AutoAccessSaveNOPrefixCommand);
        AutoAccessSettingsNOPrefixForm.setCommandListener(this);

        // --- countryForm, editera och byt önskat landsnummer i prg.

        countryForm = new Form(settingsDefaultCountryCode_DEF);

        countryTextField = new TextField(settingsDefaultSelectCountryCode_DEF,
                                         "",
                                         4,
                                         TextField.NUMERIC);

        countryBackCommand = new Command(genDefaultBack_DEF, Command.BACK,
                                         2);
        countryCancelCommand = new Command(genDefaultCancel_DEF, Command.CANCEL,
                                           3);
        countrySaveCommand = new Command(genDefaultSave_DEF, Command.OK, 1);

        countryForm.addCommand(countryBackCommand);
        countryForm.addCommand(countryCancelCommand);
        countryForm.addCommand(countrySaveCommand);
        countryForm.setCommandListener(this);

        // --- editAbsentForm, editera, DTFM-sträng, namn osv.

        editAbsentForm = new Form(absentDefaultEditPresence_DEF);

        editAbsentName_TextField = new TextField(callExtensionDefaultName_DEF,
                                                 "", 32,
                                                 TextField.INITIAL_CAPS_WORD);

        editAbsentSaveCommand = new Command(genDefaultSave_DEF, Command.OK, 1);
        editAbsentBackCommand = new Command(genDefaultBack_DEF, Command.BACK, 2);
        editAbsentCancelCommand = new Command(genDefaultCancel_DEF,
                                              Command.CANCEL, 3);

        editAbsentForm.addCommand(editAbsentBackCommand);
        editAbsentForm.addCommand(editAbsentCancelCommand);
        editAbsentForm.addCommand(editAbsentSaveCommand);
        editAbsentForm.setCommandListener(this);

        /* ================== ALERT's ================================== */

        // --- 31-dagars licens har gått ut.

        try {
            Image alertExitLicenseImage = Image.createImage(
                    "/prg_icon/exit2_64.png");
            alertExperinceLicense = new Alert("License expired",
                                              "Your license has expired\n" +
                                              "Please contact your PBX dealer"
                                              + "\n\nwww.mobisma.com",
                                              alertExitLicenseImage,
                                              AlertType.CONFIRMATION);

            alertExperinceLicense.setTimeout(Alert.FOREVER);
            licensYESCommand = new Command("Ok", Command.EXIT, 1);

            alertExperinceLicense.addCommand(licensYESCommand);
            alertExperinceLicense.setCommandListener(this);

        } catch (IOException ex5) {
        }

        // --- alertON, Visas då prg 'Mex on' är aktiv.

        try {

            Image alertInfo = Image.createImage("/prg_icon/info.png");
            alertON = new Alert(mainListAttributMexOn_DEF, "", alertInfo,
                                AlertType.INFO);
            alertON.setString(mainListAttributMexOn_DEF + "?");
            alertON.setTimeout(Alert.FOREVER);

            confirmOnYESCommand = new Command(genDefaultYes_DEF, Command.OK, 1);
            confirmOnCancelCommand = new Command(genDefaultCancel_DEF,
                                                 Command.CANCEL, 2);

            alertON.addCommand(confirmOnYESCommand);
            alertON.addCommand(confirmOnCancelCommand);
            alertON.setCommandListener(this);

        } catch (IOException ex11) {
        }

        //--- alertOFF, Visas då prg 'Mex off' är aktiv.

        try {

            Image alertMexOff = Image.createImage("/prg_icon/info.png");
            alertOFF = new Alert(mainListAttributMexOff_DEF, "", alertMexOff,
                                 AlertType.INFO);
            alertOFF.setString(mainListAttributMexOff_DEF + "?");
            alertOFF.setTimeout(Alert.FOREVER);

            confirmOffYESCommand = new Command(genDefaultYes_DEF, Command.OK, 1);
            confirmOffCancelCommand = new Command(genDefaultCancel_DEF,
                                                  Command.CANCEL, 2);

            alertOFF.addCommand(confirmOffYESCommand);
            alertOFF.addCommand(confirmOffCancelCommand);
            alertOFF.setCommandListener(this);

        } catch (IOException ex11) {
        }

        //--- alertEditSettings, Visas då något i prg sparas om.

        Image alertEditSettingImage = Image.createImage(
                "/prg_icon/save.png");
        alertEditSettings = new Alert(alertDefaultSaveChanges_DEF,
                                      alsertDefaultChangesSave_DEF,
                                      alertEditSettingImage,
                                      AlertType.CONFIRMATION);

        alertEditSettings.setTimeout(2000);

        //--- alertDebugONOFF, Visas då prg 'Debug' är ON el. OFF

        try {

            Image alertInfo = Image.createImage("/prg_icon/info.png");
            alertDebugONOFF = new Alert(alertMessageMexServerInfo_DEF, "",
                                        alertInfo,
                                        AlertType.INFO);
            alertDebugONOFF.setTimeout(1000);

        } catch (IOException ex11) {
        }

        //--- alertSendOKNOK, Visas då prg 'Sänder till webservern' är Ok el fel.

        try {

            Image alertInfo = Image.createImage("/prg_icon/info.png");
            alertSendOKNOK = new Alert(alertMessageMexServerInfo_DEF, "",
                                       alertInfo,
                                       AlertType.INFO);
            alertSendOKNOK.setTimeout(Alert.FOREVER);

        } catch (IOException ex11) {
        }

        //--- alertSENDDebug, Visas då Debug-logg skickas.

        try {

            Image alertInfo = Image.createImage("/prg_icon/info.png");
            alertSENDDebug = new Alert(alertMessageMexServerInfo_DEF,
                                       "Sending Log!\nWait for response.",
                                       alertInfo, AlertType.INFO);
            alertSENDDebug.setTimeout(1500);

        } catch (IOException ex11) {
        }

        //--- alertLogOutDebug, Visas då Debug-logg skickas.

        try {

            Image alertInfo = Image.createImage("/prg_icon/info.png");
            alertLogOutDebug = new Alert(alertMessageMexServerInfo_DEF,
                                         "Logout!", alertInfo, AlertType.INFO);
            alertLogOutDebug.setTimeout(1000);

        } catch (IOException ex11) {
        }

        //--- alertMexAlreadyONOFF, Visas då prg 'Mex on/off' redan är aktiv.

        try {

            Image alertInfo = Image.createImage("/prg_icon/info.png");
            alertMexAlreadyONOFF = new Alert(alertMessageMexServerInfo_DEF, "",
                                             alertInfo,
                                             AlertType.INFO);
            alertMexAlreadyONOFF.setTimeout(Alert.FOREVER);

        } catch (IOException ex11) {
        }

        //--- alertRestarting, Visad då prg måste startas om, tex språkbyte.


        Image alertRestartImage = Image.createImage("/prg_icon/restart.png");
        alertRestarting = new Alert(alertDefaultSaveChanges_DEF,
                                    exitDefaultRestartProgram_DEF,
                                    alertRestartImage,
                                    AlertType.CONFIRMATION);

        alertRestarting.setTimeout(Alert.FOREVER);

        /* ================== FRISTÅENDE KOMMANDON ========================= */

        GraphicsAboutCommand = new Command(version_DEF, Command.HELP, 3);
        goGraphicsBackCommand = new Command(genDefaultBack_DEF, Command.BACK, 2);
        GraphicsHelpCommand = new Command(settingsDefaultHelp_DEF, Command.HELP,
                                          3);

        /* ================== LICENS KONTROLL ============================= */

        // Om licensen är en demo-licens '1' kontrollera datumet.
        if (demo_PBX.equals("1")) {
            ControllDateTime();
        } else if (!demo_PBX.equals("1")) {

            this.ViewDateString = "Enterprise License";

        }
        dateTime = null;
        rms = null;

    } // Konstruktorn slutar här.


    /* ================== FORM settings ==================================== */

    // --- Edit Koppla samtal
    public Form getConnectEditForm() {

        connectEditForm.deleteAll();
        connectEditNameTextField.setString("");
        connectEditForm.append(connectEditNameTextField);
        connectEditExtensionTextField.setString("");
        connectEditForm.append(connectEditExtensionTextField);
        return connectEditForm;
    }

    // --- Editra 'listan' Koppla samtal
    public Form getConnectRenameEditForm() {

        connectRenameEditForm.deleteAll();
        connectRenameEditForm.append(connectEditRenameNameTextField);
        connectRenameEditForm.append(connectEditRenameExtensionTextField);
        return connectRenameEditForm;
    }

    // --- Koppla samtal
    public Form getConnectPhoneForm() {

        connectPhoneForm.deleteAll();
        connectPhoneForm.append(connectTextField);

        /*
                 try {
            connectPhoneForm.append(getRadioButton());
                 } catch (InvalidRecordIDException ex) {
                 } catch (RecordStoreNotOpenException ex) {
                 } catch (RecordStoreException ex) {
                 } catch (IOException ex) {
                 }

         */

        return connectPhoneForm;
    }

    public Form getRenameForm() {

        connectRenameForm.deleteAll();

        try {
            connectRenameForm.append(getEditButton());
        } catch (InvalidRecordIDException ex) {
        } catch (RecordStoreNotOpenException ex) {
        } catch (RecordStoreException ex) {
        } catch (IOException ex) {
        }

        return connectRenameForm;
    }

    // --- Hänvisning forms.

    public Form getAtExtForm() { // - Tillfälligt ute

        atExtForm.deleteAll();
        atExtForm.append(atExtTextField);

        return atExtForm;
    }

    public Form getBackATForm() { // - Sammanträde

        backAtForm.deleteAll();
        backAtForm.append(backAtTextField);

        return backAtForm;
    }

    public Form getOutForm() { // - Tjänsteresa

        outForm.deleteAll();
        outForm.append(outTextField);

        return outForm;
    }

    public Form getEditAbsentForm(String s) {

        editAbsentForm.deleteAll();

        DataBase_RMS rms = null;
        try {
            rms = new DataBase_RMS();
        } catch (IOException ex1) {
        } catch (RecordStoreNotOpenException ex1) {
        } catch (InvalidRecordIDException ex1) {
        } catch (RecordStoreException ex1) {
        }

        if (s.equals("1")) {

            try {
                this.absentSystemName = rms.getEditAbsentName_1();
            } catch (RecordStoreNotOpenException ex) {
            } catch (InvalidRecordIDException ex) {
            } catch (RecordStoreException ex) {
            }

        } else if (s.equals("2")) {

            try {
                this.absentSystemName = rms.getEditAbsentName_2();
            } catch (RecordStoreNotOpenException ex) {
            } catch (InvalidRecordIDException ex) {
            } catch (RecordStoreException ex) {
            }

        } else if (s.equals("3")) {

            try {
                this.absentSystemName = rms.getEditAbsentName_3();
            } catch (RecordStoreNotOpenException ex) {
            } catch (InvalidRecordIDException ex) {
            } catch (RecordStoreException ex) {
            }

        }
        editAbsentName_TextField.setString(absentSystemName);
        editAbsentForm.append(editAbsentName_TextField);

        // System.out.println("Seeeeeeeeeeeeeeeeee >>>>> " + editNEWAbsent);
        rms = null;
        return editAbsentForm;
    }

    // --- Sätter värden och kan ändra tecknet *7
    public Form getPreEditForm() throws RecordStoreNotOpenException,
            InvalidRecordIDException, RecordStoreException {

        preEditForm.deleteAll();

        String setPreEditCode = this.precode_PBX;
        preEditTextField.setString(setPreEditCode);
        preEditForm.append(preEditTextField);

        return preEditForm;
    }

    // --- Sätter värden i pinCodeSettingsForm
    public Form getPINCodeSettingsForm() {

        pinCodeSettingsForm.deleteAll();

        if (this.lineAccess_PBX.equals("NONE")) {

            this.lineAccess_PBX = "9";
        }

        pinCodeLineAccessTextField.setString(lineAccess_PBX);
        pinCodeSettingsForm.append(pinCodeLineAccessTextField);
        pinCodeSwitchBoardNumberTextField.setString(switchBoardNumber_PBX);
        pinCodeSettingsForm.append(pinCodeSwitchBoardNumberTextField);
        pinCodeExtensionNumberTextField.setString(extensionNumber_PBX);
        pinCodeSettingsForm.append(pinCodeExtensionNumberTextField);
        pinCodePinCodeNumberTextField.setString(pinCodeNumber_PBX);
        pinCodeSettingsForm.append(pinCodePinCodeNumberTextField);

        return pinCodeSettingsForm;
    }

    // --- Sätter värden i AutoAccessSettingsNOPrefixForm.
    public Form getAutoAccessNOPrefixForm() {

        AutoAccessSettingsNOPrefixForm.deleteAll();

        AutoAccessNOPrefixSwitchBoardTextField.setString(switchBoardNumber_PBX);
        AutoAccessSettingsNOPrefixForm.append(
                AutoAccessNOPrefixSwitchBoardTextField);

        return AutoAccessSettingsNOPrefixForm;
    }


    // --- Sätter värden i AutoAccessSettingsForm.
    public Form getAutoAccessSettingForm() {

        AutoAccessSettingsForm.deleteAll();

        if (this.lineAccess_PBX.equals("NONE")) {

            this.lineAccess_PBX = "9";
        }

        AutoAccessLineAccessTextField.setString(lineAccess_PBX);
        AutoAccessSettingsForm.append(AutoAccessLineAccessTextField);

        AutoAccessSwitchBoardTextField.setString(switchBoardNumber_PBX);
        AutoAccessSettingsForm.append(AutoAccessSwitchBoardTextField);

        return AutoAccessSettingsForm;

    }

    // --- Sätter värden i countryForm.
    public Form getCountryForm() {

        countryForm.deleteAll();
        try {
            countryTextField.setString(this.countryCode_PBX);
        } catch (Exception ex) {
        }
        countryForm.append(countryTextField);

        return countryForm;
    }

    // --- Login Form DeBug
    public Form getLogInForm() {

        logDataForm.deleteAll();
        logDataForm.append(logDataTextField);

        return logDataForm;
    }

    // --- Sätter värden i voiceOperatorMessageForm.
    public Form getOperatorVoiceMessageForm() {

        voiceOperatorMessageForm.deleteAll();
        voiceOperatorMessageTextField.setString(voiceMailOperator_PBX);
        voiceOperatorMessageForm.append(voiceOperatorMessageTextField);

        return voiceOperatorMessageForm;
    }

    // --- Sätter värden i voiceEditForm_PBX.
    public Form getPBXVoiceEditForm() throws RecordStoreNotOpenException,
            InvalidRecordIDException, RecordStoreException {

        voiceEditForm_PBX.deleteAll();
        voiceMailPBXTextField_PBX.setString(voiceMailSwitchboard_PBX);
        voiceEditForm_PBX.append(voiceMailPBXTextField_PBX);

        return voiceEditForm_PBX;
    }

    // --- Sätter och visar logoffGroupForm.
    public Form getLogoffGroupForm() {

        logoffGroupForm.deleteAll();
        logoffGroupForm.append(logoffGroupTextField);

        return logoffGroupForm;
    }

    // --- Sätter och visar loginGroupForm.
    public Form getLoginGroupForm() {

        loginGroupForm.deleteAll();
        loginGroupForm.append(loginGroupTextField);

        return loginGroupForm;
    }

    public Form getAllCallForwardForm() {

        allCallForwardForm.deleteAll();

        if (allCallForwardRename.equals("1")) {

            allCallForwardForm.setTitle(callForwardDefaultAllCalls_DEF);

        } else if (allCallForwardRename.equals("2")) {

            allCallForwardForm.setTitle(callForwardDefaultBusy_DEF);

        } else if (allCallForwardRename.equals("3")) {

            allCallForwardForm.setTitle(callForwardDefaultNoAnswer_DEF);

        } else if (allCallForwardRename.equals("4")) {

            allCallForwardForm.setTitle(callForwardDefaultBusyNoAnswer_DEF);

        }

        allCallForwardForm.append(allCallForwardTextField);

        return allCallForwardForm;
    }

    public Form getExternCallForwardForm() {

        externCallForwardForm.deleteAll();

        if (externCallForwardRename.equals("1")) {

            externCallForwardForm.setTitle(callForwardDefaultAllCalls_DEF);

        } else if (externCallForwardRename.equals("2")) {

            externCallForwardForm.setTitle(callForwardDefaultBusy_DEF);

        } else if (externCallForwardRename.equals("3")) {

            externCallForwardForm.setTitle(callForwardDefaultNoAnswer_DEF);

        } else if (externCallForwardRename.equals("4")) {

            externCallForwardForm.setTitle(callForwardDefaultBusyNoAnswer_DEF);

        }

        externCallForwardForm.append(externCallForwardTextField);

        return externCallForwardForm;
    }

    public Form getInternCallForwardForm() {

        internCallForwardForm.deleteAll();

        if (internCallForwardRename.equals("1")) {

            internCallForwardForm.setTitle(callForwardDefaultAllCalls_DEF);

        } else if (internCallForwardRename.equals("2")) {

            internCallForwardForm.setTitle(callForwardDefaultBusy_DEF);

        } else if (internCallForwardRename.equals("3")) {

            internCallForwardForm.setTitle(callForwardDefaultNoAnswer_DEF);

        } else if (internCallForwardRename.equals("4")) {

            internCallForwardForm.setTitle(callForwardDefaultBusyNoAnswer_DEF);

        }

        internCallForwardForm.append(internCallForwardTextField);

        return internCallForwardForm;
    }

    /* ================== LIST settings ==================================== */

    public List getCallForwardList() {

        // --- CallforwardList

        callForwardList = new List(callForwardDefault_DEF, Choice.IMPLICIT);

        backCallForwardCommand = new Command(genDefaultBack_DEF, Command.BACK,
                                             2);
        try {
            Image imageCallForward = Image.createImage(
                    "/prg_icon/vidarekoppling24.png");

            callForwardList.append(callForwardDefaultAllCalls_DEF,
                                   imageCallForward);
            callForwardList.append(callForwardDefaultExternCalls_DEF,
                                   imageCallForward);
            callForwardList.append(callForwardDefaultInternCalls_DEF,
                                   imageCallForward);

            callForwardList.addCommand(backCallForwardCommand);
            callForwardList.setCommandListener(this);

        } catch (IOException ex) {
            System.out.println("Unable to Find or Read .png file");
        }

        return callForwardList;
    }

    public List getAllCallForwardList() {

        // --- AllCallforwardList

        allCallForwardList = new List(callForwardDefaultAllCalls_DEF,
                                      Choice.IMPLICIT); // skapar en lista
        backAllCallForwardCommand = new Command(genDefaultBack_DEF,
                                                Command.BACK, 2);

        try {
            Image imageAllCallForward = Image.createImage(
                    "/prg_icon/vidarekoppling24.png");

            allCallForwardList.append(callForwardDefaultRemove_DEF,
                                      imageAllCallForward);
            allCallForwardList.append(callForwardDefaultDontDisturb_DEF,
                                      imageAllCallForward);
            allCallForwardList.append(callForwardDefaultAllCalls_DEF,
                                      imageAllCallForward);
            allCallForwardList.append(callForwardDefaultBusy_DEF,
                                      imageAllCallForward);
            allCallForwardList.append(callForwardDefaultNoAnswer_DEF,
                                      imageAllCallForward);
            allCallForwardList.append(callForwardDefaultBusyNoAnswer_DEF,
                                      imageAllCallForward);

            allCallForwardList.addCommand(backAllCallForwardCommand);
            allCallForwardList.setCommandListener(this);

        } catch (IOException ex) {
            System.out.println("Unable to Find or Read .png file");
        }

        return allCallForwardList;
    }

    public List getExternCallForwardList() {

        // --- ExternCallforwardList

        externCallForwardList = new List(callForwardDefaultExternCalls_DEF,
                                         Choice.IMPLICIT); // skapar en lista

        backExternCallForwardCommand = new Command(genDefaultBack_DEF,
                Command.BACK, 2);

        try {
            Image imageExternCallForward = Image.createImage(
                    "/prg_icon/vidarekoppling24.png");

            externCallForwardList.append(callForwardDefaultRemove_DEF,
                                         imageExternCallForward);
            externCallForwardList.append(callForwardDefaultDontDisturb_DEF,
                                         imageExternCallForward);
            externCallForwardList.append(callForwardDefaultAllCalls_DEF,
                                         imageExternCallForward);
            externCallForwardList.append(callForwardDefaultBusy_DEF,
                                         imageExternCallForward);
            externCallForwardList.append(callForwardDefaultNoAnswer_DEF,
                                         imageExternCallForward);
            externCallForwardList.append(callForwardDefaultBusyNoAnswer_DEF,
                                         imageExternCallForward);

            externCallForwardList.addCommand(backExternCallForwardCommand);
            externCallForwardList.setCommandListener(this);

        } catch (IOException ex) {
            System.out.println("Unable to Find or Read .png file");
        }

        return externCallForwardList;
    }

    public List getInternCallForwardList() {

        // --- InternCallforwardList

        internCallForwardList = new List(callForwardDefaultInternCalls_DEF,
                                         Choice.IMPLICIT); // skapar en lista

        backInternCallForwardCommand = new Command(genDefaultBack_DEF,
                Command.BACK, 2);

        try {
            Image imageInternCallForward = Image.createImage(
                    "/prg_icon/vidarekoppling24.png");

            internCallForwardList.append(callForwardDefaultRemove_DEF,
                                         imageInternCallForward);
            internCallForwardList.append(callForwardDefaultDontDisturb_DEF,
                                         imageInternCallForward);
            internCallForwardList.append(callForwardDefaultAllCalls_DEF,
                                         imageInternCallForward);
            internCallForwardList.append(callForwardDefaultBusy_DEF,
                                         imageInternCallForward);
            internCallForwardList.append(callForwardDefaultNoAnswer_DEF,
                                         imageInternCallForward);
            internCallForwardList.append(callForwardDefaultBusyNoAnswer_DEF,
                                         imageInternCallForward);

            internCallForwardList.addCommand(backInternCallForwardCommand);
            internCallForwardList.setCommandListener(this);

        } catch (IOException ex) {
            System.out.println("Unable to Find or Read .png file");
        }

        return internCallForwardList;
    }

    public List getAbsentEditList() {

        DataBase_RMS rms = null;
        try {
            rms = new DataBase_RMS();
        } catch (IOException ex1) {
        } catch (RecordStoreNotOpenException ex1) {
        } catch (InvalidRecordIDException ex1) {
        } catch (RecordStoreException ex1) {
        }

        // --- absentEditList, välj att editera attribut 1 eller 2.

        absentEditList = new List(absentDefaultEditPresence_DEF,
                                  Choice.IMPLICIT);
        absentEditList.append("", null);
        absentEditList.append("", null);
        absentEditList.append("", null);

        editAbsentListBackCommand = new Command(genDefaultBack_DEF,
                                                Command.BACK, 1);
        editAbsentListCancelCommand = new Command(genDefaultCancel_DEF,
                                                  Command.CANCEL, 2);

        absentEditList.addCommand(editAbsentListBackCommand);
        absentEditList.addCommand(editAbsentListCancelCommand);
        absentEditList.setCommandListener(this);

        String absent_1 = null;
        try {
            absent_1 = rms.getEditAbsentName_1();
        } catch (RecordStoreNotOpenException ex) {
        } catch (InvalidRecordIDException ex) {
        } catch (RecordStoreException ex) {
        }
        String absent_2 = null;
        try {
            absent_2 = rms.getEditAbsentName_2();
        } catch (RecordStoreNotOpenException ex2) {
        } catch (InvalidRecordIDException ex2) {
        } catch (RecordStoreException ex2) {
        }
        String absent_3 = null;
        try {
            absent_3 = rms.getEditAbsentName_3();
        } catch (RecordStoreNotOpenException ex2) {
        } catch (InvalidRecordIDException ex2) {
        } catch (RecordStoreException ex2) {
        }

        absentEditList.set(0, "1: " + absent_1, null);
        absentEditList.set(1, "2: " + absent_2, null);
        absentEditList.set(2, "3: " + absent_3, null);

        rms = null;
        return absentEditList;

    }

    public Alert getAlertExit() {

        // --- Exit alert. Visas då prg avslutas.
        try {
            Image alertExitImage = Image.createImage("/prg_icon/exit2_64.png");
            alertExit = new Alert(genDefaultExit_DEF,
                                  "",
                                  alertExitImage, AlertType.CONFIRMATION);

            alertExit.setTimeout(Alert.FOREVER);

            confirmExitYESCommand = new Command(genDefaultYes_DEF,
                                                Command.EXIT, 1);
            confirmExitNOCommand = new Command(exitDefaultNo_DEF,
                                               Command.OK, 2);

            alertExit.addCommand(confirmExitYESCommand);
            alertExit.addCommand(confirmExitNOCommand);
            alertExit.setCommandListener(this);

        } catch (IOException ex5) {
        }

        DataBase_RMS rms = null;
        try {
            rms = new DataBase_RMS();
        } catch (IOException ex) {
        } catch (RecordStoreNotOpenException ex) {
        } catch (InvalidRecordIDException ex) {
        } catch (RecordStoreException ex) {
        }

        try {
            if (rms.getMexONOFF().equals("1")) {
                alertExit.setString(programExitON_DEF);
            }
        } catch (RecordStoreNotOpenException ex1) {
        } catch (InvalidRecordIDException ex1) {
        } catch (RecordStoreException ex1) {
        }
        try {
            if (rms.getMexONOFF().equals("0")) {

                alertExit.setString(programExitOFF_DEF);

            }
        } catch (RecordStoreNotOpenException ex2) {
        } catch (InvalidRecordIDException ex2) {
        } catch (RecordStoreException ex2) {
        }

        rms = null;

        return alertExit;

    }

    public List getAbsentList() {

        DataBase_RMS rms = null;
        try {
            rms = new DataBase_RMS();
        } catch (IOException ex1) {
        } catch (RecordStoreNotOpenException ex1) {
        } catch (InvalidRecordIDException ex1) {
        } catch (RecordStoreException ex1) {
        }

        try {
            this.absentStatus = rms.getAbsentStatus();
        } catch (RecordStoreNotOpenException ex2) {
        } catch (InvalidRecordIDException ex2) {
        } catch (RecordStoreException ex2) {
        }

        //--- AbsentList, lista för hänvisning i prg.

        absentList = new List("", Choice.IMPLICIT); // skapar en lista

        try {
            if (rms.getAbsentStatus().equals("0")) {
                absentList.setTicker(null);
                absentStatusTicker = new Ticker(" ");
                absentList.setTicker(absentStatusTicker);

            } else if (!rms.getAbsentStatus().equals("0")) {
                absentList.setTicker(null);
                absentStatusTicker = new Ticker(absentStatus);
                absentList.setTicker(absentStatusTicker);

            }
        } catch (RecordStoreNotOpenException ex3) {
        } catch (InvalidRecordIDException ex3) {
        } catch (RecordStoreException ex3) {
        }

        BackCommandAbsentList = new Command(genDefaultBack_DEF, Command.BACK, 2);

        try {

            Image image1c = Image.createImage(
                    "/prg_icon/taborthanvisning24.png");
            Image image2c = Image.createImage("/prg_icon/ute24.png");
            Image image3c = Image.createImage("/prg_icon/upptagen24.png");
            Image image4c = Image.createImage("/prg_icon/systemphone24.png");
            Image image5c = Image.createImage("/prg_icon/tillbakakl24.png");
            Image image6c = Image.createImage("/prg_icon/tillbakaden24.png");
            Image image7c = Image.createImage("/prg_icon/konference24.png");
            Image image8c = Image.createImage("/prg_icon/samtalslista24.png");

            // - Ta bort hänvisning
            absentList.append(absentDefaultRemovePresence_DEF, image1c);
            // - Strax tillbaka
            absentList.append(absentDeafaultWillReturnSoon_DEF, image2c);
            // - Gått för dagen
            absentList.append(absentDefaultGoneHome_DEF, image3c);
            // - Finns på anknytning
            absentList.append(absentDefaultAtExt_DEF, image4c);
            // - Åter klockan
            absentList.append(absentDefaultBackAt_DEF, image5c);
            // - Åter den
            absentList.append(absentDefaultOutUntil_DEF, image6c);
            // - På möte
            absentList.append(absentDefaultInAMeeting_DEF, image7c);
            // - System attribut 1
            absentList.append("", null);
            // - System attribut 2
            absentList.append("", null);
            // - Personligt attriut
            absentList.append("", null);

            absentList.addCommand(BackCommandAbsentList);
            absentList.setCommandListener(this);

            String absent_1 = rms.getEditAbsentName_1();
            String absent_2 = rms.getEditAbsentName_2();
            String absent_3 = rms.getEditAbsentName_3();

            if (absent_1.equals("0")) {

                absentList.set(7, editAbsentName_1, image8c); // mex on

            } else if (!absent_1.equals("0")) {

                absentList.set(7, absent_1, image8c); // mex on

            }
            if (absent_2.equals("0")) {

                absentList.set(8, editAbsentName_2, image8c); // mex on

            } else if (!absent_2.equals("0")) {

                absentList.set(8, absent_2, image8c); // mex on

            }
            if (absent_3.equals("0")) {

                absentList.set(9, editAbsentName_3, image8c); // mex off

            } else if (!absent_3.equals("0")) {

                absentList.set(9, absent_3, image8c); // mex off

            }

        } catch (IOException ex) {
            System.out.println("Unable to Find or Read .png file");
        } catch (RecordStoreNotOpenException ex) {
            /** @todo Handle this exception */
        } catch (InvalidRecordIDException ex) {
            /** @todo Handle this exception */
        } catch (RecordStoreException ex) {
            /** @todo Handle this exception */
        }

        rms = null;
        return absentList;
    }


    public List getMainList() {

        /* ================== HUVUDMENY/LISTA ============================ */

        DataBase_RMS rms = null;
        try {
            rms = new DataBase_RMS();
        } catch (IOException ex1) {
        } catch (RecordStoreNotOpenException ex1) {
        } catch (InvalidRecordIDException ex1) {
        } catch (RecordStoreException ex1) {
        }
        // Huvudlistan, förstamenyn visas när prg startat.
        mainListEditCommand = new Command(genDefaultEdit_DEF, Command.OK, 1);
        mainListaboutMobismaCommand = new Command(settingsDefaultAbout_DEF,
                                                  Command.OK, 2);
        mainListExitCommand = new Command(genDefaultExit_DEF, Command.OK, 3);

        mainList = new List(prg_Name, Choice.IMPLICIT);

        try {
            Image image1a = Image.createImage("/prg_icon/systemphone24.png");
            Image image2a = Image.createImage("/prg_icon/hanvisa24.png");
            Image image3a = Image.createImage("/prg_icon/vidarekoppling24.png");
            Image image4a = Image.createImage("/prg_icon/rostbrevlada24.png");
            Image image5a = Image.createImage("/prg_icon/konference24.png");
            Image image6a = Image.createImage("/prg_icon/on24.png");
            Image image7a = Image.createImage("/prg_icon/off24.png");
            Image image8a = Image.createImage("/prg_icon/minimera24.png");

            // - Koppla samtal
            mainList.append(callForwardTransfer_DEF, image1a);
            // - Hänvisa
            mainList.append(absentDefaultSetPresence_DEF, image2a);
            // - Vidarekoppla
            mainList.append(callForwardDefault_DEF, image3a);
            // - Röstbrevlådan
            mainList.append(voiceMailDefaultVoiceMail_DEF, image4a);
            // - Grupper
            mainList.append(groupsDefaultGroups_DEF, image5a);
            // - Mex on, Mex off
            mainList.append("", null);

            mainList.append(genDefaultMinimise_DEF, image8a);

            mainList.addCommand(mainListExitCommand);
            mainList.addCommand(mainListEditCommand);
            mainList.addCommand(mainListaboutMobismaCommand);
            mainList.setCommandListener(this);

            // kod ...
            String mex = rms.getMexONOFF();

            if (mex.equals("1")) {

                mainList.set(5, mainListAttributMexOn_DEF, image6a); // mex on
            } else if (mex.equals("0")) {

                mainList.set(5, mainListAttributMexOff_DEF, image7a); // mex off

            }

        } catch (IOException ex) {
            System.out.println("Unable to Find or Read .png file");
        } catch (RecordStoreNotOpenException ex) {
            /** @todo Handle this exception */
        } catch (InvalidRecordIDException ex) {
            /** @todo Handle this exception */
        } catch (RecordStoreException ex) {
            /** @todo Handle this exception */
        }

        rms = null;
        return mainList;
    }

    // --- Sätter och visar listan
    public List getLanguageList() {

        return language_List;
    }

    // --- Sätter och visar listan
    public List getSettingsList() {

        return pbx_List;
    }


    /* ================== ITEM ChoiceGroup ==================================== */

    public ChoiceGroup getRadioButton() throws IOException,
            RecordStoreNotOpenException, InvalidRecordIDException,
            RecordStoreException {

        MDataStore.DataBase_RMS rms = new DataBase_RMS();

        String s = this.extensionDefaultAddNew_DEF;
        MModel.InternNumber intern = new InternNumber();
        intern.setInternButtonName(s);

        radioButtons.deleteAll();

        radioButtons.append(rms.getInternName(51), null);
        radioButtons.append(rms.getInternName(52), null);
        radioButtons.append(rms.getInternName(53), null);
        radioButtons.append(rms.getInternName(54), null);
        radioButtons.append(rms.getInternName(55), null);
        radioButtons.append(rms.getInternName(56), null);
        radioButtons.append(rms.getInternName(57), null);
        radioButtons.append(rms.getInternName(58), null);
        radioButtons.append(rms.getInternName(59), null);
        radioButtons.append(rms.getInternName(60), null);
        radioButtons.append(rms.getInternName(61), null);
        radioButtons.append(rms.getInternName(62), null);
        radioButtons.append(rms.getInternName(63), null);
        radioButtons.append(rms.getInternName(64), null);
        radioButtons.append(rms.getInternName(65), null);
        radioButtons.append(rms.getInternName(66), null);
        radioButtons.append(rms.getInternName(67), null);
        radioButtons.append(rms.getInternName(68), null);
        radioButtons.append(rms.getInternName(69), null);
        radioButtons.append(rms.getInternName(70), null);
        radioButtons.append(rms.getInternName(71), null);
        radioButtons.append(rms.getInternName(72), null);
        radioButtons.append(rms.getInternName(73), null);
        radioButtons.append(rms.getInternName(74), null);
        defaultIndex = radioButtons.append(rms.getInternName(75), null);
        radioButtons.setSelectedIndex(defaultIndex, true);
        radioButtons.setLabel(enterDefaultEnterExtension_DEF);

        String s1 = rms.getInternName(51);
        String s2 = rms.getInternName(52);
        String s3 = rms.getInternName(53);
        String s4 = rms.getInternName(54);
        String s5 = rms.getInternName(55);

        String s6 = rms.getInternName(56);
        String s7 = rms.getInternName(57);
        String s8 = rms.getInternName(58);
        String s9 = rms.getInternName(59);
        String s10 = rms.getInternName(60);

        String s11 = rms.getInternName(61);
        String s12 = rms.getInternName(62);
        String s13 = rms.getInternName(63);
        String s14 = rms.getInternName(64);
        String s15 = rms.getInternName(65);

        String s16 = rms.getInternName(66);
        String s17 = rms.getInternName(67);
        String s18 = rms.getInternName(68);
        String s19 = rms.getInternName(69);
        String s20 = rms.getInternName(70);

        String s21 = rms.getInternName(71);
        String s22 = rms.getInternName(72);
        String s23 = rms.getInternName(73);
        String s24 = rms.getInternName(74);
        String s25 = rms.getInternName(75);

        radioButtons.set(0, s1, null);
        radioButtons.set(1, s2, null);
        radioButtons.set(2, s3, null);
        radioButtons.set(3, s4, null);
        radioButtons.set(4, s5, null);

        radioButtons.set(5, s6, null);
        radioButtons.set(6, s7, null);
        radioButtons.set(7, s8, null);
        radioButtons.set(8, s9, null);
        radioButtons.set(9, s10, null);

        radioButtons.set(10, s11, null);
        radioButtons.set(11, s12, null);
        radioButtons.set(12, s13, null);
        radioButtons.set(13, s14, null);
        radioButtons.set(14, s15, null);

        radioButtons.set(15, s16, null);
        radioButtons.set(16, s17, null);
        radioButtons.set(17, s18, null);
        radioButtons.set(18, s19, null);
        radioButtons.set(19, s20, null);

        radioButtons.set(20, s21, null);
        radioButtons.set(21, s22, null);
        radioButtons.set(22, s23, null);
        radioButtons.set(23, s24, null);
        radioButtons.set(24, s25, null);

        intern = null;
        rms = null;
        return radioButtons;
    }

    public ChoiceGroup getEditButton() throws IOException,
            RecordStoreNotOpenException, InvalidRecordIDException,
            RecordStoreException {

        MDataStore.DataBase_RMS rms = new DataBase_RMS();

        editButtons.deleteAll();

        editButtons.append(rms.getInternName(51), null);
        editButtons.append(rms.getInternName(52), null);
        editButtons.append(rms.getInternName(53), null);
        editButtons.append(rms.getInternName(54), null);
        editButtons.append(rms.getInternName(55), null);
        editButtons.append(rms.getInternName(56), null);
        editButtons.append(rms.getInternName(57), null);
        editButtons.append(rms.getInternName(58), null);
        editButtons.append(rms.getInternName(59), null);
        editButtons.append(rms.getInternName(60), null);
        editButtons.append(rms.getInternName(61), null);
        editButtons.append(rms.getInternName(62), null);
        editButtons.append(rms.getInternName(63), null);
        editButtons.append(rms.getInternName(64), null);
        editButtons.append(rms.getInternName(65), null);
        editButtons.append(rms.getInternName(66), null);
        editButtons.append(rms.getInternName(67), null);
        editButtons.append(rms.getInternName(68), null);
        editButtons.append(rms.getInternName(69), null);
        editButtons.append(rms.getInternName(70), null);
        editButtons.append(rms.getInternName(71), null);
        editButtons.append(rms.getInternName(72), null);
        editButtons.append(rms.getInternName(73), null);
        editButtons.append(rms.getInternName(74), null);
        editButtonIndex = editButtons.append(rms.getInternName(75), null);
        editButtons.setSelectedIndex(editButtonIndex, true);
        editButtons.setLabel(genDefaultEdit_DEF);

        String s1 = rms.getInternName(51);
        String s2 = rms.getInternName(52);
        String s3 = rms.getInternName(53);
        String s4 = rms.getInternName(54);
        String s5 = rms.getInternName(55);

        String s6 = rms.getInternName(56);
        String s7 = rms.getInternName(57);
        String s8 = rms.getInternName(58);
        String s9 = rms.getInternName(59);
        String s10 = rms.getInternName(60);

        String s11 = rms.getInternName(61);
        String s12 = rms.getInternName(62);
        String s13 = rms.getInternName(63);
        String s14 = rms.getInternName(64);
        String s15 = rms.getInternName(65);

        String s16 = rms.getInternName(66);
        String s17 = rms.getInternName(67);
        String s18 = rms.getInternName(68);
        String s19 = rms.getInternName(69);
        String s20 = rms.getInternName(70);

        String s21 = rms.getInternName(71);
        String s22 = rms.getInternName(72);
        String s23 = rms.getInternName(73);
        String s24 = rms.getInternName(74);
        String s25 = rms.getInternName(75);

        editButtons.set(0, s1, null);
        editButtons.set(1, s2, null);
        editButtons.set(2, s3, null);
        editButtons.set(3, s4, null);
        editButtons.set(4, s5, null);

        editButtons.set(5, s6, null);
        editButtons.set(6, s7, null);
        editButtons.set(7, s8, null);
        editButtons.set(8, s9, null);
        editButtons.set(9, s10, null);

        editButtons.set(10, s11, null);
        editButtons.set(11, s12, null);
        editButtons.set(12, s13, null);
        editButtons.set(13, s14, null);
        editButtons.set(14, s15, null);

        editButtons.set(15, s16, null);
        editButtons.set(16, s17, null);
        editButtons.set(17, s18, null);
        editButtons.set(18, s19, null);
        editButtons.set(19, s20, null);

        editButtons.set(20, s21, null);
        editButtons.set(21, s22, null);
        editButtons.set(22, s23, null);
        editButtons.set(23, s24, null);
        editButtons.set(24, s25, null);

        rms = null;
        return editButtons;
    }


    public void itemStateChanged(Item item) {

        MModel.InternNumber intern = new InternNumber();

        if (item == radioButtons) {

            // ID int-värde av valt nummer 0 - 24 av id-nummren
            int ID = radioButtons.getSelectedIndex();

            // Sätter rätt id för hämta rätt anknytning, tex plats 0 = 51 i DB.
            String person = null;
            try {
                person = intern.getInternPerson(ID);
            } catch (InvalidRecordIDException ex1) {
            } catch (RecordStoreNotOpenException ex1) {
            } catch (RecordStoreException ex1) {
            } catch (IOException ex1) {
            }

            System.out.println("Returvärde person >> " + person);

            // Om ID platsen == '0' i DB öppna edit-formen | editera
            if (person.equals("0") || person.equals(null) ||
                person.equals(extensionDefaultAddNew_DEF)) {

                Display.getDisplay(this).setCurrent(getConnectEditForm());
                ID = intern.getInternID(ID);
                this.IDInternNumber = ID;

            }
            // Om ID platsen != '0' så koppla samtal
            else if (!person.equals("0")) { // Om ID platsen != '0' i DB >> Koppla samtalet.

                /* Plockar ut index och person sorterar bort allt utom >> siffror
                 Använder sedan siffrorna getPersonIDs för att koppla samtal */
                String getPersonIDs = radioButtons.getString(radioButtons.
                        getSelectedIndex());
                String internNumber = new String(getPersonIDs);
                MModel.SortClass sort = new SortClass();
                internNumber = sort.sortCharToDigits(internNumber);
                sort = null;

                Methods methods = null;
                try {
                    methods = new Methods();
                } catch (InvalidRecordIDException ex) {
                } catch (RecordStoreNotOpenException ex) {
                } catch (RecordStoreException ex) {
                } catch (IOException ex) {
                }
                methods.getConnectPhoneCallSEND = internNumber.trim();
                methods.ConnectPhoneCall();
                methods = null;

                System.out.println("INDEX ID >> " + ID);
                System.out.println("PersonID  >> " + getPersonIDs);
                System.out.println("Internnummer (kopplar till)  >> " +
                                   internNumber);

            }

        } else if (item == editButtons) {

            // ID int-värde av valt nummer 0 - 19 av id-nummren
            int ID = editButtons.getSelectedIndex();

            // Sätter rätt id för hämta rätt anknytning, tex plats 0 = 51 i DB.
            String person = null;
            try {
                person = intern.getInternPerson(ID);
            } catch (InvalidRecordIDException ex1) {
            } catch (RecordStoreNotOpenException ex1) {
            } catch (RecordStoreException ex1) {
            } catch (IOException ex1) {
            }

            System.out.println("Returvärde person >> " + person);
            MModel.SortClass sort = new SortClass();
            String name = sort.sortDigitsToCharacter(person);
            String number = sort.sortCharToDigits(person);
            sort = null;

            connectEditRenameNameTextField.setString(name.trim());
            connectEditRenameExtensionTextField.setString(number.trim());

            // Om ID platsen == '0' i DB öppna edit-formen | editera
            Display.getDisplay(this).setCurrent(getConnectRenameEditForm());
            ID = intern.getInternID(ID);
            this.IDInternNumber = ID;

        }

        intern = null;
    }

    /* ===== Huvudklassens tre metoder, main osv. ========================== */

    public void startApp() {

        startSplash();

    }

    public void pauseApp() {

    }

    public void destroyApp(boolean unconditional) {

    }


    // --- Sätter java.lang.string
    public String toString(String b) {

        String s = b;

        return s;
    }


    /* ===== List-Kommandon =========================================== */

    public void commandAction(Command c, Displayable d) {
        Thread th = new Thread(this);
        thCmd = c;
        th.start();
        if (d.equals(mainList)) {
            if (c == List.SELECT_COMMAND) {
                if (d.equals(mainList)) {
                    switch (((List) d).getSelectedIndex()) {

                    case 0: // Koppla samtal

                        Display.getDisplay(this).setCurrent(getConnectPhoneForm());

                        break;

                    case 1: // Hänvisa

                        Display.getDisplay(this).setCurrent(getAbsentList());

                        break;

                    case 2: // Vidarekoppla

                        Display.getDisplay(this).setCurrent(getCallForwardList());

                        break;

                    case 3: // Röstbrevlåda

                        try {
                            rms = new DataBase_RMS();
                        } catch (IOException ex1) {
                        } catch (RecordStoreNotOpenException ex1) {
                        } catch (InvalidRecordIDException ex1) {
                        } catch (RecordStoreException ex1) {
                        }
                        try {
                            pbx_type = rms.getPBXType();
                        } catch (RecordStoreNotOpenException ex2) {
                        } catch (InvalidRecordIDException ex2) {
                        } catch (RecordStoreException ex2) {
                        }
                        rms = null;

                        if (pbx_type.equals("3")) {

                            String start_mexon_01 = "a" + "," +
                                    switchBoardNumber_PBX + "," +
                                    voiceMailSwitchboard_PBX + "p" +
                                    "#6*" +
                                    extensionNumber_PBX + ",";

                            sendMexOnOffMessage(start_mexon_01);

                            System.out.println("Auto access Röstbrevlåda >> " +
                                               start_mexon_01);

                        }

                        if (pbx_type.equals("5")) {

                            String start_mexon_01 = "a" + "," +
                                    switchBoardNumber_PBX + "," +
                                    "*47" +
                                    extensionNumber_PBX +
                                    pinCodeNumber_PBX +
                                    voiceMailSwitchboard_PBX + "p" +
                                    "#6*" +
                                    extensionNumber_PBX + ",";

                            sendMexOnOffMessage(start_mexon_01);

                            System.out.println(
                                    " PIN-Code access Röstbrevlådan >> " +
                                    start_mexon_01);

                        }

                        /* Senare tillägg i panasonic växeln */
                        // Display.getDisplay(this).setCurrent(voiceMailPBXList);


                        break;

                    case 4: // Grupper

                        Display.getDisplay(this).setCurrent(groupList);

                        break;

                    case 5: // Mex på

                        DataBase_RMS rms = null;
                        try {
                            rms = new DataBase_RMS();
                        } catch (IOException ex1) {
                        } catch (RecordStoreNotOpenException ex1) {
                        } catch (InvalidRecordIDException ex1) {
                        } catch (RecordStoreException ex1) {
                        }
                        try {
                            mexOnOff = rms.getMexONOFF();
                        } catch (RecordStoreNotOpenException ex2) {
                        } catch (InvalidRecordIDException ex2) {
                        } catch (RecordStoreException ex2) {
                        }
                        rms = null;

                        if (mexOnOff.equals("0")) {

                            Display.getDisplay(this).setCurrent(alertON);

                        }

                        if (mexOnOff.equals("1")) {

                            Display.getDisplay(this).setCurrent(alertOFF);

                        }

                        break;

                    case 6: // Minimera

                        try {
                            methods = new Methods();
                        } catch (InvalidRecordIDException ex) {
                        } catch (RecordStoreNotOpenException ex) {
                        } catch (RecordStoreException ex) {
                        } catch (IOException ex) {
                        }
                        methods.Minimize();
                        methods = null;

                        break;

                    }
                }

            }

        }

        // Vidarekoppling *****************************************

        if (d.equals(callForwardList)) {
            if (c == List.SELECT_COMMAND) {
                if (d.equals(callForwardList)) {
                    switch (((List) d).getSelectedIndex()) {
                    case 0:

                        Display.getDisplay(this).setCurrent(
                                getAllCallForwardList());

                        break;

                    case 1:

                        Display.getDisplay(this).setCurrent(
                                getExternCallForwardList());

                        break;

                    case 2:

                        Display.getDisplay(this).setCurrent(
                                getInternCallForwardList());

                        break;

                    }
                }

            }

        }
        if (d.equals(allCallForwardList)) {
            if (c == List.SELECT_COMMAND) {
                if (d.equals(allCallForwardList)) {
                    switch (((List) d).getSelectedIndex()) {
                    case 0:

                        Methods methods = null;
                        try {
                            methods = new Methods();
                        } catch (InvalidRecordIDException ex) {
                        } catch (RecordStoreNotOpenException ex) {
                        } catch (RecordStoreException ex) {
                        } catch (IOException ex) {
                        }

                        methods.getAbsentSEND = "100";
                        methods.PanasonicSetAbsent();
                        methods = null;

                        break;

                    case 1:

                        methods = null;
                        try {
                            methods = new Methods();
                        } catch (InvalidRecordIDException ex4) {
                        } catch (RecordStoreNotOpenException ex4) {
                        } catch (RecordStoreException ex4) {
                        } catch (IOException ex4) {
                        }
                        methods.getAbsentSEND = "101";
                        methods.PanasonicSetAbsent();
                        methods = null;

                        break;

                    case 2:
                        this.allCallForwardRename = "1";
                        Display.getDisplay(this).setCurrent(
                                getAllCallForwardForm());

                        break;

                    case 3:
                        this.allCallForwardRename = "2";
                        Display.getDisplay(this).setCurrent(
                                getAllCallForwardForm());

                        break;

                    case 4:
                        this.allCallForwardRename = "3";
                        Display.getDisplay(this).setCurrent(
                                getAllCallForwardForm());

                        break;

                    case 5:
                        this.allCallForwardRename = "4";
                        Display.getDisplay(this).setCurrent(
                                getAllCallForwardForm());

                        break;

                    }
                }

            }

        }

        if (d.equals(externCallForwardList)) {
            if (c == List.SELECT_COMMAND) {
                if (d.equals(externCallForwardList)) {
                    switch (((List) d).getSelectedIndex()) {
                    case 0:

                        Methods methods = null;
                        try {
                            methods = new Methods();
                        } catch (InvalidRecordIDException ex) {
                        } catch (RecordStoreNotOpenException ex) {
                        } catch (RecordStoreException ex) {
                        } catch (IOException ex) {
                        }
                        methods.getAbsentSEND = "110";
                        methods.PanasonicSetAbsent();
                        methods = null;

                        break;

                    case 1:

                        methods = null;
                        try {
                            methods = new Methods();
                        } catch (InvalidRecordIDException ex4) {
                        } catch (RecordStoreNotOpenException ex4) {
                        } catch (RecordStoreException ex4) {
                        } catch (IOException ex4) {
                        }
                        methods.getAbsentSEND = "111";
                        methods.PanasonicSetAbsent();
                        methods = null;

                        break;

                    case 2:

                        this.externCallForwardRename = "1";
                        Display.getDisplay(this).setCurrent(
                                getExternCallForwardForm());

                        break;

                    case 3:

                        this.externCallForwardRename = "2";
                        Display.getDisplay(this).setCurrent(
                                getExternCallForwardForm());

                        break;

                    case 4:

                        this.externCallForwardRename = "3";
                        Display.getDisplay(this).setCurrent(
                                getExternCallForwardForm());

                        break;

                    case 5:

                        this.externCallForwardRename = "4";
                        Display.getDisplay(this).setCurrent(
                                getExternCallForwardForm());

                        break;

                    }
                }

            }

        }

        if (d.equals(internCallForwardList)) {
            if (c == List.SELECT_COMMAND) {
                if (d.equals(internCallForwardList)) {
                    switch (((List) d).getSelectedIndex()) {
                    case 0:

                        Methods methods = null;
                        try {
                            methods = new Methods();
                        } catch (InvalidRecordIDException ex) {
                        } catch (RecordStoreNotOpenException ex) {
                        } catch (RecordStoreException ex) {
                        } catch (IOException ex) {
                        }
                        methods.getAbsentSEND = "120";
                        methods.PanasonicSetAbsent();
                        methods = null;

                        break;

                    case 1:

                        methods = null;
                        try {
                            methods = new Methods();
                        } catch (InvalidRecordIDException ex4) {
                        } catch (RecordStoreNotOpenException ex4) {
                        } catch (RecordStoreException ex4) {
                        } catch (IOException ex4) {
                        }
                        methods.getAbsentSEND = "121";
                        methods.PanasonicSetAbsent();
                        methods = null;

                        break;

                    case 2:

                        this.internCallForwardRename = "1";
                        Display.getDisplay(this).setCurrent(
                                getInternCallForwardForm());

                        break;

                    case 3:

                        this.internCallForwardRename = "2";
                        Display.getDisplay(this).setCurrent(
                                getInternCallForwardForm());

                        break;

                    case 4:

                        this.internCallForwardRename = "3";
                        Display.getDisplay(this).setCurrent(
                                getInternCallForwardForm());

                        break;

                    case 5:

                        this.internCallForwardRename = "4";
                        Display.getDisplay(this).setCurrent(
                                getInternCallForwardForm());

                        break;

                    }
                }

            }

        }

        // Hänvisning *********************************************

        if (d.equals(absentList)) {
            if (c == List.SELECT_COMMAND) {
                if (d.equals(absentList)) {
                    switch (((List) d).getSelectedIndex()) {

                    case 0: // Remove Presence

                        DataBase_RMS rms = null;
                        try {
                            rms = new DataBase_RMS();
                        } catch (IOException ex8) {
                        } catch (RecordStoreNotOpenException ex8) {
                        } catch (InvalidRecordIDException ex8) {
                        } catch (RecordStoreException ex8) {
                        }

                        rms.setAbsentStatus("0");

                        Display.getDisplay(this).setCurrent(getAbsentList());
                        Methods methods = null;
                        try {
                            methods = new Methods();
                        } catch (InvalidRecordIDException ex) {
                        } catch (RecordStoreNotOpenException ex) {
                        } catch (RecordStoreException ex) {
                        } catch (IOException ex) {
                        }
                        methods.removePanasonicAbsent();
                        methods = null;
                        rms = null;
                        break;

                    case 1: // Will return soon

                        setAbsentStatusString(absentDeafaultWillReturnSoon_DEF);
                        Display.getDisplay(this).setCurrent(getAbsentList());
                        methods = null;
                        try {
                            methods = new Methods();
                        } catch (InvalidRecordIDException ex4) {
                        } catch (RecordStoreNotOpenException ex4) {
                        } catch (RecordStoreException ex4) {
                        } catch (IOException ex4) {
                        }
                        methods.getAbsentSEND = "501#";
                        methods.PanasonicSetAbsent();
                        methods = null;
                        break;

                    case 2: // Gone Home

                        setAbsentStatusString(absentDefaultGoneHome_DEF);
                        Display.getDisplay(this).setCurrent(getAbsentList());
                        methods = null;
                        try {
                            methods = new Methods();
                        } catch (InvalidRecordIDException ex4) {
                        } catch (RecordStoreNotOpenException ex4) {
                        } catch (RecordStoreException ex4) {
                        } catch (IOException ex4) {
                        }
                        methods.getAbsentSEND = "502#";
                        methods.PanasonicSetAbsent();
                        methods = null;

                        break;

                    case 3: // At Ext.

                        Display.getDisplay(this).setCurrent(getAtExtForm());

                        break;

                    case 4: // Back At

                        Display.getDisplay(this).setCurrent(getBackATForm());

                        break;

                    case 5: // Out Until

                        Display.getDisplay(this).setCurrent(getOutForm());

                        break;

                    case 6: // In a Meeting

                        setAbsentStatusString(absentDefaultInAMeeting_DEF);
                        Display.getDisplay(this).setCurrent(getAbsentList());
                        methods = null;
                        try {
                            methods = new Methods();
                        } catch (InvalidRecordIDException ex4) {
                        } catch (RecordStoreNotOpenException ex4) {
                        } catch (RecordStoreException ex4) {
                        } catch (IOException ex4) {
                        }
                        methods.getAbsentSEND = "506#";
                        methods.PanasonicSetAbsent();
                        methods = null;

                        break;

                    case 7: // Systemattribut 1, ny hänvisning 'rename'

                        int p = absentList.getSelectedIndex();
                        System.out.println("getselected index >> " + p);

                        if (p == 7 && this.editAbsentDTMF_1.equals("0")) {
                            String s1 = "1";
                            Display.getDisplay(this).setCurrent(
                                    getEditAbsentForm(s1));
                            this.editNEWAbsent = "1";

                        }

                        else {

                            rms = null;
                            try {
                                rms = new DataBase_RMS();
                            } catch (IOException ex5) {
                            } catch (RecordStoreNotOpenException ex5) {
                            } catch (InvalidRecordIDException ex5) {
                            } catch (RecordStoreException ex5) {
                            }
                            String absentName_1 = null;
                            try {
                                absentName_1 = rms.getEditAbsentName_1();
                            } catch (RecordStoreNotOpenException ex6) {
                            } catch (InvalidRecordIDException ex6) {
                            } catch (RecordStoreException ex6) {
                            }

                            setAbsentStatusString(absentName_1);
                            Display.getDisplay(this).setCurrent(getAbsentList());

                            methods = null;
                            try {
                                methods = new Methods();
                            } catch (InvalidRecordIDException ex4) {
                            } catch (RecordStoreNotOpenException ex4) {
                            } catch (RecordStoreException ex4) {
                            } catch (IOException ex4) {
                            }
                            methods.getAbsentSEND = "507#";
                            methods.PanasonicSetAbsent();
                            methods = null;

                        }

                        break;

                    case 8: // Systemattribut 2, ny hänvisning 'rename'

                        int pp = absentList.getSelectedIndex();
                        System.out.println("getselected index >> " + pp);

                        if (pp == 8 && this.editAbsentDTMF_2.equals("0")) {
                            String s2 = "2";
                            Display.getDisplay(this).setCurrent(
                                    getEditAbsentForm(s2));
                            this.editNEWAbsent = "2";
                        }

                        else {

                            rms = null;
                            try {
                                rms = new DataBase_RMS();
                            } catch (IOException ex5) {
                            } catch (RecordStoreNotOpenException ex5) {
                            } catch (InvalidRecordIDException ex5) {
                            } catch (RecordStoreException ex5) {
                            }
                            String absentName_2 = null;
                            try {
                                absentName_2 = rms.getEditAbsentName_2();
                            } catch (RecordStoreNotOpenException ex6) {
                            } catch (InvalidRecordIDException ex6) {
                            } catch (RecordStoreException ex6) {
                            }

                            setAbsentStatusString(absentName_2);
                            Display.getDisplay(this).setCurrent(getAbsentList());

                            methods = null;
                            try {
                                methods = new Methods();
                            } catch (InvalidRecordIDException ex4) {
                            } catch (RecordStoreNotOpenException ex4) {
                            } catch (RecordStoreException ex4) {
                            } catch (IOException ex4) {
                            }
                            methods.getAbsentSEND = "508#";
                            methods.PanasonicSetAbsent();
                            methods = null;
                            rms = null;
                        }

                        break;

                    case 9: // Personligt, ny hänvisning 'rename'

                        int ppp = absentList.getSelectedIndex();
                        System.out.println("getselected index >> " + ppp);

                        if (ppp == 9 && this.editAbsentDTMF_3.equals("0")) {
                            String s3 = "3";
                            Display.getDisplay(this).setCurrent(
                                    getEditAbsentForm(s3));
                            this.editNEWAbsent = "3";
                        }

                        else {

                            rms = null;
                            try {
                                rms = new DataBase_RMS();
                            } catch (IOException ex9) {
                            } catch (RecordStoreNotOpenException ex9) {
                            } catch (InvalidRecordIDException ex9) {
                            } catch (RecordStoreException ex9) {
                            }
                            String absentName_3 = null;
                            try {
                                absentName_3 = rms.getEditAbsentName_3();
                            } catch (RecordStoreNotOpenException ex6) {
                            } catch (InvalidRecordIDException ex6) {
                            } catch (RecordStoreException ex6) {
                            }

                            setAbsentStatusString(absentName_3);
                            Display.getDisplay(this).setCurrent(getAbsentList());

                            methods = null;
                            try {
                                methods = new Methods();
                            } catch (InvalidRecordIDException ex4) {
                            } catch (RecordStoreNotOpenException ex4) {
                            } catch (RecordStoreException ex4) {
                            } catch (IOException ex4) {
                            }
                            methods.getAbsentSEND = "509#";
                            methods.PanasonicSetAbsent();
                            methods = null;

                        }

                        break;

                    }
                }

            }

        }

        if (d.equals(absentEditList)) {
            if (c == List.SELECT_COMMAND) {
                if (d.equals(absentEditList)) {
                    switch (((List) d).getSelectedIndex()) {

                    case 0:
                        String s1 = "1";
                        Display.getDisplay(this).setCurrent(getEditAbsentForm(
                                s1));
                        this.editNEWAbsent = "1";
                        System.out.println("Seeeeeeeeeeeeeeeeee >>>>> " +
                                           editNEWAbsent);

                        break;

                    case 1:
                        String s2 = "2";
                        Display.getDisplay(this).setCurrent(getEditAbsentForm(
                                s2));
                        this.editNEWAbsent = "2";
                        System.out.println("Seeeeeeeeeeeeeeeeee >>>>> " +
                                           editNEWAbsent);

                        break;

                    case 2:
                        String s3 = "3";
                        Display.getDisplay(this).setCurrent(getEditAbsentForm(
                                s3));
                        this.editNEWAbsent = "3";
                        System.out.println("Seeeeeeeeeeeeeeeeee >>>>> " +
                                           editNEWAbsent);

                        break;

                    }
                }

            }

        }

        if (d.equals(voiceMailPBXList)) {
            if (c == List.SELECT_COMMAND) {
                if (d.equals(voiceMailPBXList)) {
                    switch (((List) d).getSelectedIndex()) {

                    case 0: // Beställ

                        try {
                            methods = new Methods();
                        } catch (InvalidRecordIDException ex) {
                        } catch (RecordStoreNotOpenException ex) {
                        } catch (RecordStoreException ex) {
                        } catch (IOException ex) {
                        }
                        methods.voiceMailSet();
                        methods = null;

                        break;

                    case 1: // Avbeställ

                        try {
                            methods = new Methods();
                        } catch (InvalidRecordIDException ex) {
                        } catch (RecordStoreNotOpenException ex) {
                        } catch (RecordStoreException ex) {
                        } catch (IOException ex) {
                        }
                        methods.voiceMailRemove();
                        methods = null;

                        break; // Lyssna

                    case 2:

                        try {
                            methods = new Methods();
                        } catch (InvalidRecordIDException ex) {
                        } catch (RecordStoreNotOpenException ex) {
                        } catch (RecordStoreException ex) {
                        } catch (IOException ex) {
                        }
                        methods.voiceMailListen();
                        methods = null;

                        break;

                    }
                }

            }

        }

        if (d.equals(debug_List)) {
            if (c == List.SELECT_COMMAND) {
                if (d.equals(debug_List)) {
                    switch (((List) d).getSelectedIndex()) {

                    case 0: // Debug On

                        Thread d_on = new Thread() {

                            public void run() {

                                try {
                                    String don = "d," + "1";

                                    sendMexOnOffMessage(don);

                                    System.out.println("Degub ON >> " + don);

                                } catch (Exception ex) {
                                }

                            }
                        };
                        d_on.start();

                        break;

                    case 1: // Debug Off

                        Thread d_off = new Thread() {

                            public void run() {

                                try {
                                    String doff = "d," + "0";

                                    sendMexOnOffMessage(doff);

                                    System.out.println("Degub OFF >> " + doff);

                                } catch (Exception ex) {
                                }

                            }
                        };
                        d_off.start();

                        break;

                    case 2: // Send Log

                        Thread send_log = new Thread() {

                            public void run() {

                                try {

                                    sendLogdata();

                                    System.out.println("Send log >> ");

                                } catch (Exception ex) {
                                }

                            }
                        };
                        send_log.start();

                        Display.getDisplay(this).setCurrent(alertSENDDebug);

                        break;

                    }
                }
            }

        }

        if (d.equals(pbx_List)) {
            if (c == List.SELECT_COMMAND) {
                if (d.equals(pbx_List)) {
                    switch (((List) d).getSelectedIndex()) {

                    case 0: // Access PBX

                        Display.getDisplay(this).setCurrent(pbx_List_type);

                        break;

                    case 1: // Operatör röstbrevlådan

                        Display.getDisplay(this).setCurrent(
                                getOperatorVoiceMessageForm());

                        break;
                    case 2: // Redigera röstbrevlådan

                        try {
                            Display.getDisplay(this).setCurrent(
                                    getPBXVoiceEditForm());
                        } catch (InvalidRecordIDException ex13) {
                        } catch (RecordStoreNotOpenException ex13) {
                        } catch (RecordStoreException ex13) {
                        }

                        break
                                ;

                    case 3: // Redigera attribut

                        Display.getDisplay(this).setCurrent(getAbsentEditList());

                        break;

                    case 4: // Redigera tecken

                        try {
                            Display.getDisplay(this).setCurrent(getPreEditForm());
                        } catch (InvalidRecordIDException ex3) {
                        } catch (RecordStoreNotOpenException ex3) {
                        } catch (RecordStoreException ex3) {
                        }

                        break
                                ;

                    case 5: // Språk

                        Display.getDisplay(this).setCurrent(getLanguageList());

                        break;

                    case 6: // System

                        Display.getDisplay(this).setCurrent(getLogInForm());

                        break;

                    }
                }

            }

        }
        if (d.equals(pbx_List_type)) {
            if (c == List.SELECT_COMMAND) {
                if (d.equals(pbx_List_type)) {
                    switch (((List) d).getSelectedIndex()) {
                    case 0:

                        Display.getDisplay(this).setCurrent(linePrefix_List);

                        break;

                    case 1:

                        Display.getDisplay(this).setCurrent(
                                getPINCodeSettingsForm());

                        break;

                    }
                }

            }

        }

        if (d.equals(linePrefix_List)) {
            if (c == List.SELECT_COMMAND) {
                if (d.equals(linePrefix_List)) {
                    switch (((List) d).getSelectedIndex()) {

                    case 0: // AutoAccess med linjeprefix

                        Display.getDisplay(this).setCurrent(
                                getAutoAccessSettingForm());

                        break;

                    case 1: // AutoAccess utan linjeprefix

                        Display.getDisplay(this).setCurrent(
                                getAutoAccessNOPrefixForm());

                        break;

                    }
                }

            }

        }

        if (d.equals(groupList)) {
            if (c == List.SELECT_COMMAND) {
                if (d.equals(groupList)) {
                    switch (((List) d).getSelectedIndex()) {

                    case 0: // inlogg alla grupper

                        System.out.println("Inlogg alla grupper");

                        try {
                            methods = new Methods();
                        } catch (InvalidRecordIDException ex) {
                        } catch (RecordStoreNotOpenException ex) {
                        } catch (RecordStoreException ex) {
                        } catch (IOException ex) {
                        }
                        methods.getAbsentSEND = "361*";
                        methods.PanasonicSetAbsent();
                        methods = null;

                        break;

                    case 1: // urlogg alla grupper

                        System.out.println("Urlogg alla grupper");

                        try {
                            methods = new Methods();
                        } catch (InvalidRecordIDException ex) {
                        } catch (RecordStoreNotOpenException ex) {
                        } catch (RecordStoreException ex) {
                        } catch (IOException ex) {
                        }
                        methods.getAbsentSEND = "360*";
                        methods.PanasonicSetAbsent();
                        methods = null;

                        break;

                    case 2: // inlogg grupp

                        System.out.println("Inlogg grupp");
                        Display.getDisplay(this).setCurrent(getLoginGroupForm());

                        break;

                    case 3: // urlogg grupp

                        System.out.println("Urlogg grupp");
                        Display.getDisplay(this).setCurrent(getLogoffGroupForm());

                        break;

                    }
                }

            }

        }

        if (d.equals(language_List)) {
            if (c == List.SELECT_COMMAND) {
                if (d.equals(language_List)) {
                    switch (((List) d).getSelectedIndex()) {

                    case 0: // English >> Default
                        this.lang_PBX = "2";
                        Display.getDisplay(this).setCurrent(getCountryForm());

                        break;

                    case 1: // Språk_2
                        this.lang_PBX = iconNumber_PBX;
                        Display.getDisplay(this).setCurrent(getCountryForm());

                        break;

                    }
                }

            }

        }

    }

    /* ===== Knapp-Kommandon =========================================== */

    public void run() {

        try {

            if (thCmd.getCommandType() == Command.EXIT) {
                notifyDestroyed();

            }

            /* ------- Send - Commands --------------------- */

            else if (thCmd == licensYESCommand) {

                notifyDestroyed();

            }

            else if (thCmd == atExtSendCommand
                     || thCmd == backAtSendCommand
                     || thCmd == outSendCommand) {

                String absentNUM = "";
                String getFromTextField = "";
                String absentSEND = "";

                if (thCmd == atExtSendCommand) {
                    absentNUM = "4";
                    getFromTextField = atExtTextField.getString();
                    atExtTextField.setString("");
                } else if (thCmd == backAtSendCommand) {
                    absentNUM = "5";
                    getFromTextField = backAtTextField.getString();
                    backAtTextField.setString("");
                } else if (thCmd == outSendCommand) {
                    absentNUM = "6";
                    getFromTextField = outTextField.getString();
                    outTextField.setString("");
                }

                if (!getFromTextField.equals("")) {

                    if (absentNUM.equals("4")) {

                        absentSEND = "503" + getFromTextField + "#";

                        String extension = absentDefaultAtExt_DEF + " " +
                                           getFromTextField;
                        setAbsentStatusString(extension);
                        Display.getDisplay(this).setCurrent(getAbsentList());

                        MModel.Methods methods = new Methods();
                        methods.getAbsentSEND = absentSEND;
                        methods.PanasonicSetAbsent();
                        methods = null;

                    }

                    getAtExtForm();

                }
                if (!getFromTextField.equals("")) {

                    if (absentNUM.equals("5")) {

                        absentSEND = "504" + getFromTextField + "#";

                        String backAtTime = absentDefaultBackAt_DEF + " " +
                                            getFromTextField;
                        setAbsentStatusString(backAtTime);
                        Display.getDisplay(this).setCurrent(getAbsentList());

                        MModel.Methods methods = new Methods();
                        methods.getAbsentSEND = absentSEND;
                        methods.PanasonicSetAbsent();
                        methods = null;

                    }

                    getBackATForm();

                }
                if (!getFromTextField.equals("")) {

                    if (absentNUM.equals("6")) {

                        absentSEND = "505" + getFromTextField + "#";

                        String dateBack = absentDefaultOutUntil_DEF + " " +
                                          getFromTextField;
                        setAbsentStatusString(dateBack);
                        Display.getDisplay(this).setCurrent(getAbsentList());

                        MModel.Methods methods = new Methods();
                        methods.getAbsentSEND = absentSEND;
                        methods.PanasonicSetAbsent();
                        methods = null;

                    }

                    getOutForm();

                }

            }

            /* ------- Login - Commands --------------------- */

            else if (thCmd == logDataLogInCommand) {

                if (logDataTextField.getString().equals("")) {

                    Display.getDisplay(this).setCurrent(getLogInForm());

                } else if (logDataTextField.getString().equals("4321")) {

                    Display.getDisplay(this).setCurrent(debug_List);

                }

                logDataTextField.setString("");
            }

            /* ------- Send - Commands --------------------- */

            // Koppla samtal send kommando
            else if (thCmd == connectSendCommand) {

                if (!connectTextField.getString().equals("")) {

                    MModel.Methods methods = new Methods();
                    methods.getConnectPhoneCallSEND = connectTextField.
                            getString();
                    methods.ConnectPhoneCall();
                    methods = null;
                    connectTextField.setString("");
                }

                getConnectPhoneForm();
            }

            else if (thCmd == connectResumeCommand) {

                    MModel.Methods methods = new Methods();
                    methods.getConnectPhoneCallSEND = "";
                    methods.ConnectPhoneCall();
                    methods = null;
                    connectTextField.setString("");
            }


            else if (thCmd == allCallForwardSendCommand) {

                MModel.Methods methods = new Methods();
                String allCallForward = allCallForwardTextField.getString();

                if (!allCallForwardTextField.getString().equals("")) {

                    if (allCallForwardRename.equals("1")) {

                        methods.getAbsentSEND = "102" + allCallForward + "#";
                    } else if (allCallForwardRename.equals("2")) {

                        methods.getAbsentSEND = "103" + allCallForward + "#";

                    } else if (allCallForwardRename.equals("3")) {

                        methods.getAbsentSEND = "104" + allCallForward + "#";

                    } else if (allCallForwardRename.equals("4")) {

                        methods.getAbsentSEND = "105" + allCallForward + "#";

                    }

                    methods.PanasonicSetAbsent();
                    methods = null;

                    allCallForwardTextField.setString("");

                }

                getAllCallForwardForm();

            } else if (thCmd == externCallForwardSendCommand) {

                MModel.Methods methods = new Methods();
                String externCallForward = externCallForwardTextField.getString();

                if (!externCallForwardTextField.getString().equals("")) {

                    if (externCallForwardRename.equals("1")) {

                        methods.getAbsentSEND = "112" + externCallForward + "#";
                    } else if (externCallForwardRename.equals("2")) {

                        methods.getAbsentSEND = "113" + externCallForward + "#";

                    } else if (externCallForwardRename.equals("3")) {

                        methods.getAbsentSEND = "114" + externCallForward + "#";

                    } else if (externCallForwardRename.equals("4")) {

                        methods.getAbsentSEND = "115" + externCallForward + "#";

                    }

                    methods.PanasonicSetAbsent();
                    methods = null;

                    externCallForwardTextField.setString("");

                }

                getExternCallForwardForm();

            } else if (thCmd == internCallForwardSendCommand) {

                MModel.Methods methods = new Methods();
                String internCallForward = internCallForwardTextField.getString();

                if (!internCallForwardTextField.getString().equals("")) {

                    if (internCallForwardRename.equals("1")) {

                        methods.getAbsentSEND = "122" + internCallForward + "#";
                    } else if (internCallForwardRename.equals("2")) {

                        methods.getAbsentSEND = "123" + internCallForward + "#";

                    } else if (internCallForwardRename.equals("3")) {

                        methods.getAbsentSEND = "124" + internCallForward + "#";

                    } else if (internCallForwardRename.equals("4")) {

                        methods.getAbsentSEND = "125" + internCallForward + "#";

                    }

                    methods.PanasonicSetAbsent();
                    methods = null;

                    internCallForwardTextField.setString("");

                }

                getInternCallForwardForm();

            }

            // Login send kommando
            else if (thCmd == loginGroupSendCommand) {

                MModel.Methods methods = new Methods();
                String groupLogin = loginGroupTextField.getString();

                if (!loginGroupTextField.getString().equals("")) {

                    methods.getAbsentSEND = "361" + groupLogin;
                    methods.PanasonicSetAbsent();
                    methods = null;

                    loginGroupTextField.setString("");

                }

                getLoginGroupForm();

            }
            // Logoff send kommando
            else if (thCmd == logoffGroupSendCommand) {

                MModel.Methods methods = new Methods();
                String groupLogoff = logoffGroupTextField.getString();

                if (!logoffGroupTextField.getString().equals("")) {

                    methods.getAbsentSEND = "360" + groupLogoff;
                    methods.PanasonicSetAbsent();
                    methods = null;

                    logoffGroupTextField.setString("");

                }

                getLogoffGroupForm();

            }

            /* ------- Koppla samtal kommandon ------------- */

            else if (thCmd == connectEditRenameCommand) {

                Display.getDisplay(this).setCurrent(getRenameForm());

            }

            /* ------- Save - Commands --------------------- */

            else if (thCmd == connectEditRenameSaveCommand) {

                if (!connectEditRenameNameTextField.getString().equals("")) {

                    if (!connectEditRenameExtensionTextField.getString().equals(
                            "")) {

                        Display.getDisplay(this).setCurrent(this.
                                alertEditSettings,
                                getMainList());

                        MDataStore.DataBase_RMS rms = new DataBase_RMS();

                        String a = connectEditRenameNameTextField.getString();
                        String b = ": ";
                        String c = connectEditRenameExtensionTextField.
                                   getString();

                        String person = a + b + c;
                        int p = this.IDInternNumber;
                        rms.setInternName(person, p);

                        System.out.println(rms.getInternName(p));
                        System.out.println("this.IDInternNumber >> " +
                                           this.IDInternNumber);

                        rms.setDataStore();
                        rms.upDateDataStore();
                        rms = null;

                    }
                }

                getConnectRenameEditForm();

            }

            // Sparar värden för Intern-nummer

            else if (thCmd == connectEditSaveCommand) {

                if (!connectEditNameTextField.getString().equals("")) {

                    if (!connectEditExtensionTextField.getString().equals("")) {

                        Display.getDisplay(this).setCurrent(this.
                                alertEditSettings,
                                getMainList());

                        MDataStore.DataBase_RMS rms = new DataBase_RMS();

                        String a = connectEditNameTextField.getString();
                        String b = ": ";
                        String c = connectEditExtensionTextField.getString();

                        String person = a + b + c;
                        int p = this.IDInternNumber;
                        rms.setInternName(person, p);

                        System.out.println(rms.getInternName(p));
                        System.out.println("this.IDInternNumber >> " +
                                           this.IDInternNumber);

                        rms.setDataStore();
                        rms.upDateDataStore();
                        rms = null;

                    }
                }

                getConnectEditForm();

            }

            // Sparar nya värden för editera dynamiska attribut. (hänvisning)
            else if (thCmd == editAbsentSaveCommand) {

                MDataStore.DataBase_RMS rms = new DataBase_RMS();

                if (editNEWAbsent.equals("1")) {

                    String name_1 = editAbsentName_TextField.getString();
                    String dtmf_1 = "507#";

                    if (name_1.equals("") || dtmf_1.equals("") ||
                        name_1.equals("") && dtmf_1.equals("")) {
                        String s1 = "1";
                        Display.getDisplay(this).setCurrent(getEditAbsentForm(
                                s1));

                    } else if (!name_1.equals("") && !dtmf_1.equals("")) {

                        Display.getDisplay(this).setCurrent(alertEditSettings,
                                getMainList());

                        rms.setRename_1("1");
                        rms.setEditAbsentName_1(name_1);
                        rms.setEditAbsentDTMF_1(dtmf_1);
                        this.editAbsentDTMF_1 = dtmf_1;

                        if (editHHTTMMTT.equals("1")) {

                            rms.setHHMMTTMM_1("1");

                        } else if (editHHTTMMTT.equals("2")) {

                            rms.setHHMMTTMM_1("2");

                        }

                    }

                    editAbsentName_TextField.setString("");

                }

                if (editNEWAbsent.equals("2")) {

                    String name_2 = editAbsentName_TextField.getString();
                    String dtmf_2 = "508#";

                    if (name_2.equals("") || dtmf_2.equals("") ||
                        name_2.equals("") && dtmf_2.equals("")) {
                        String s2 = "2";
                        Display.getDisplay(this).setCurrent(getEditAbsentForm(
                                s2));

                    } else if (!name_2.equals("") && !dtmf_2.equals("")) {

                        Display.getDisplay(this).setCurrent(alertEditSettings,
                                getMainList());

                        rms.setRename_2("1");
                        rms.setEditAbsentName_2(name_2);
                        rms.setEditAbsentDTMF_2(dtmf_2);
                        this.editAbsentDTMF_2 = dtmf_2;

                        if (editHHTTMMTT.equals("1")) {

                            rms.setHHMMTTMM_2("1");

                        } else if (editHHTTMMTT.equals("2")) {

                            rms.setHHMMTTMM_2("2");

                        }

                    }

                    editAbsentName_TextField.setString("");

                }

                if (editNEWAbsent.equals("3")) {

                    String name_3 = editAbsentName_TextField.getString();
                    String dtmf_3 = "509#";

                    if (name_3.equals("") || dtmf_3.equals("") ||
                        name_3.equals("") && dtmf_3.equals("")) {
                        String s3 = "3";
                        Display.getDisplay(this).setCurrent(getEditAbsentForm(
                                s3));

                    } else if (!name_3.equals("") && !dtmf_3.equals("")) {

                        Display.getDisplay(this).setCurrent(alertEditSettings,
                                getMainList());

                        rms.setRename_3("1");
                        rms.setEditAbsentName_3(name_3);
                        rms.setEditAbsentDTMF_3(dtmf_3);
                        this.editAbsentDTMF_3 = dtmf_3;

                        if (editHHTTMMTT.equals("1")) {

                            rms.setHHMMTTMM_3("1");

                        } else if (editHHTTMMTT.equals("2")) {

                            rms.setHHMMTTMM_3("2");

                        }

                    }

                    editAbsentName_TextField.setString("");

                }

                rms = null;
            }
            // Sparar nya värden för autoaccessNOPrefix
            else if (thCmd == AutoAccessSaveNOPrefixCommand) {

                if (!AutoAccessNOPrefixSwitchBoardTextField.getString().equals(
                        "")) {

                    Display.getDisplay(this).setCurrent(alertEditSettings,
                            getMainList());
                    MDataStore.DataBase_RMS rms = new DataBase_RMS();
                    this.lineAccess_PBX = "NONE";
                    this.switchBoardNumber_PBX =
                            AutoAccessNOPrefixSwitchBoardTextField.getString();
                    rms.setLineAccess(lineAccess_PBX);
                    rms.setSwitchBoardNumber(switchBoardNumber_PBX);
                    rms.setDataStore();
                    rms.upDateDataStore();
                    this.lineAccess_PBX = rms.getAccessNumber();
                    rms.setAccessTypeTo("3");

                    rms = null;

                    MModel.Methods methods = new Methods();
                    methods.RefreshServer();
                    methods = null;
                }

                getAutoAccessNOPrefixForm();

            }
            // Sparar nya värden för autoaccess
            else if (thCmd == AutoAccessSaveCommand) {

                if (!AutoAccessLineAccessTextField.getString().equals("")) {

                    if (!AutoAccessSwitchBoardTextField.getString().equals("")) {

                        Display.getDisplay(this).setCurrent(alertEditSettings,
                                getMainList());
                        MDataStore.DataBase_RMS rms = new DataBase_RMS();
                        this.lineAccess_PBX = AutoAccessLineAccessTextField.
                                              getString();
                        this.switchBoardNumber_PBX =
                                AutoAccessSwitchBoardTextField.getString();
                        rms.setLineAccess(lineAccess_PBX);
                        rms.setSwitchBoardNumber(switchBoardNumber_PBX);
                        rms.setAccessTypeTo("3");

                        rms.setDataStore();
                        rms.upDateDataStore();
                        rms = null;

                        MModel.Methods methods = new Methods();
                        methods.RefreshServer();
                        methods = null;

                    }

                }

                getAutoAccessSettingForm();

            }
            // Sparar nya värden för PIN-Code
            else if (thCmd == pinCodeSaveCommand) {

                if (!pinCodeLineAccessTextField.getString().equals("")) {

                    if (!pinCodeSwitchBoardNumberTextField.getString().equals(
                            "")) {

                        if (!pinCodeExtensionNumberTextField.getString().equals(
                                "")) {

                            if (!pinCodePinCodeNumberTextField.getString().
                                equals("")) {

                                Display.getDisplay(this).setCurrent(
                                        alertEditSettings, getMainList());
                                MDataStore.DataBase_RMS rms = new DataBase_RMS();
                                this.lineAccess_PBX =
                                        pinCodeLineAccessTextField.getString();
                                this.switchBoardNumber_PBX =
                                        pinCodeSwitchBoardNumberTextField.
                                        getString();
                                this.extensionNumber_PBX =
                                        pinCodeExtensionNumberTextField.
                                        getString();
                                this.pinCodeNumber_PBX =
                                        pinCodePinCodeNumberTextField.getString();

                                rms.setLineAccess(lineAccess_PBX);
                                rms.setSwitchBoardNumber(switchBoardNumber_PBX);
                                rms.setExtensionNumber(extensionNumber_PBX);
                                rms.setPINCode(pinCodeNumber_PBX);
                                rms.setAccessTypeTo("5");

                                rms.setDataStore();
                                rms.upDateDataStore();
                                rms = null;

                                MModel.Methods methods = new Methods();
                                methods.RefreshPINCODEServer();
                                methods = null;

                            }
                        }
                    }
                }

                getPINCodeSettingsForm();

            }

            // Sparar nya värden för PRE-edit code
            else if (thCmd == preEditSaveCommand) {

                if (!preEditTextField.getString().equals("")) {

                    Display.getDisplay(this).setCurrent(alertEditSettings,
                            getMainList());
                    MDataStore.DataBase_RMS rms = new DataBase_RMS();
                    this.precode_PBX = preEditTextField.getString();

                    rms.setPreCode(precode_PBX);
                    rms.setDataStore();
                    rms.upDateDataStore();
                    rms = null;

                }

                getPreEditForm();

            }
            // this.lang_PBX = "2"; this.lang_PBX = iconNumber_PBX;
            // Sparar värden för Språk.
            else if (thCmd == countrySaveCommand) {

                if (!countryTextField.getString().equals("")) {

                    Display.getDisplay(this).setCurrent(alertRestarting,
                            getMainList());

                    MDataStore.DataBase_RMS rms = new DataBase_RMS();

                    rms.setLanguage(this.lang_PBX);
                    countryCode_PBX = countryTextField.getString();
                    rms.setCountryCode(countryCode_PBX);

                    String rename_1 = rms.getRename_1();
                    String rename_2 = rms.getRename_2();
                    String rename_3 = rms.getRename_3();

                    if (rename_1.equals("0") && rms.getLanguage().equals("2")) {

                        rms.setEditAbsentName_1(language.
                                                absentDefaultSystemAttOne_1);

                    }
                    if (rename_2.equals("0") && rms.getLanguage().equals("2")) {

                        rms.setEditAbsentName_2(language.
                                                absentDeafaultSystemAttTwo_1);

                    }
                    if (rename_3.equals("0") && rms.getLanguage().equals("2")) {

                        rms.setEditAbsentName_3(language.
                                                absentDefaultPersonalAtt_1);

                    }

                    if (rename_1.equals("0") && !rms.getLanguage().equals("2")) {

                        rms.setEditAbsentName_1(language.
                                                absentDefaultSystemAttOne_2);

                    }
                    if (rename_2.equals("0") && !rms.getLanguage().equals("2")) {

                        rms.setEditAbsentName_2(language.
                                                absentDeafaultSystemAttTwo_2);

                    }
                    if (rename_3.equals("0") && !rms.getLanguage().equals("2")) {

                        rms.setEditAbsentName_3(language.
                                                absentDefaultPersonalAtt_2);

                    }
                    rms.setAbsentStatus("0");

                    rms = null;
                }
                getCountryForm();
            }
            // Sparar värden för voicemail pbx.
            else if (thCmd == voiceEditSaveCommand_PBX) {

                if (!voiceMailPBXTextField_PBX.getString().equals("")) {

                    Display.getDisplay(this).setCurrent(alertEditSettings,
                            getMainList());
                    MDataStore.DataBase_RMS rms = new DataBase_RMS();
                    voiceMailSwitchboard_PBX = voiceMailPBXTextField_PBX.
                                               getString();
                    rms.setVoiceMailSwitchBoard(voiceMailSwitchboard_PBX);
                    rms = null;

                }

                getPBXVoiceEditForm();

            }
            // Sparar värden för voicemail operatör
            else if (thCmd == voiceOperatorMessageSaveCommand) {

                if (!voiceOperatorMessageTextField.getString().equals("")) {

                    MDataStore.DataBase_RMS rms = new DataBase_RMS();
                    Display.getDisplay(this).setCurrent(alertEditSettings,
                            getMainList());
                    voiceMailOperator_PBX = voiceOperatorMessageTextField.
                                            getString();
                    rms.setVoiceMailOperator(voiceMailOperator_PBX);
                    rms = null;
                }

                getOperatorVoiceMessageForm();
            }

            /* ------- View - Commands Graphics --------------------- */

            else if (thCmd == mainListEditCommand) {

                Display.getDisplay(this).setCurrent(getSettingsList());

            } else if (thCmd == GraphicsAboutCommand) {

                GraphicsBackCommand = new Command(genDefaultBack_DEF,
                                                  Command.OK, 2);
                String sendName = prg_Name + " " + version_DEF;
                Displayable k = new AboutUs(sendName);
                Display.getDisplay(this).setCurrent(k);
                k.addCommand(GraphicsBackCommand);
                k.setCommandListener(this);

            } else if (thCmd == GraphicsHelpCommand) {

                GraphicsBackCommand = new Command(genDefaultBack_DEF,
                                                  Command.OK, 2);
                String sendName = prg_Name + " " + version_DEF;
                Displayable k = new HelpInfo(sendName);
                Display.getDisplay(this).setCurrent(k);
                k.addCommand(GraphicsBackCommand);
                k.setCommandListener(this);

            } else if (thCmd == GraphicsBackCommand) {

                String sendName = prg_Name + " " + version_DEF;
                Displayable k = new ServerNumber(sendName, ViewDateString,
                                                 device_brands, deveice_model,
                                                 pbx_name);
                Display.getDisplay(this).setCurrent(k);
                k.addCommand(goGraphicsBackCommand);
                k.addCommand(GraphicsAboutCommand);
                k.addCommand(GraphicsHelpCommand);
                k.setCommandListener(this);

            } else if (thCmd == mainListaboutMobismaCommand) {

                String sendName = prg_Name + " " + version_DEF;
                Displayable k = new ServerNumber(sendName, ViewDateString,
                                                 device_brands, deveice_model,
                                                 pbx_name);
                Display.getDisplay(this).setCurrent(k);
                k.addCommand(goGraphicsBackCommand);
                k.addCommand(GraphicsAboutCommand);
                k.addCommand(GraphicsHelpCommand);
                k.setCommandListener(this);

            }

            /* ------- Back - Commands --------------------- */

            else if (thCmd == allCallForwardBackCommand) {

                Display.getDisplay(this).setCurrent(getAllCallForwardList());
            }

            else if (thCmd == externCallForwardBackCommand) {

                Display.getDisplay(this).setCurrent(getExternCallForwardList());
            }

            else if (thCmd == internCallForwardBackCommand) {

                Display.getDisplay(this).setCurrent(getInternCallForwardList());
            }

            else if (thCmd == backAllCallForwardCommand
                     || thCmd == backExternCallForwardCommand
                     || thCmd == backInternCallForwardCommand) {

                Display.getDisplay(this).setCurrent(getCallForwardList());
            }

            else if (thCmd == editAbsentBackCommand) {

                Display.getDisplay(this).setCurrent(getAbsentEditList());
            }

            else if (thCmd == connectEditRenameBackCommand) {

                Display.getDisplay(this).setCurrent(getRenameForm());

            }

            else if (thCmd == connectEditBackCommand
                     || thCmd == connectRenameBackCommand
                     || thCmd == connectEditRenameCancelCommand) {

                Display.getDisplay(this).setCurrent(getConnectPhoneForm());

            }

            else if (thCmd == groupBackCommand
                     || thCmd == confirmExitNOCommand
                     || thCmd == voiceEditCancelCommand_PBX
                     || thCmd == voiceOperatorMessageCancelCommand
                     || thCmd == pbx_ListCancelCommand
                     || thCmd == BackCommandAbsentList
                     || thCmd == AutoAccessCancelCommand
                     || thCmd == AutoAccessCancelNOPrefixCommand
                     || thCmd == editAbsentListCancelCommand
                     || thCmd == editAbsentCancelCommand
                     || thCmd == goGraphicsBackCommand
                     || thCmd == voiceMailPBXListBackCommand
                     || thCmd == connectBackCommand
                     || thCmd == connectEditCancelCommand
                     || thCmd == confirmOnCancelCommand
                     || thCmd == confirmOffCancelCommand
                     || thCmd == pbx_List_typeCancelCommand
                     || thCmd == pinCodeCancelCommand
                     || thCmd == preEditCancelCommand
                     || thCmd == backCallForwardCommand) {

                Display.getDisplay(this).setCurrent(getMainList());

            } else if (thCmd == linePrefixBackCommand
                       || thCmd == voiceOperatorMessageBackCommand
                       || thCmd == voiceEditBackcommand_PBX
                       || thCmd == editAbsentListBackCommand
                       || thCmd == languageListBackCommand
                       || thCmd == countryCancelCommand
                       || thCmd == logDataCancelLogInCommand
                       || thCmd == pbx_List_typeBackCommand
                       || thCmd == preEditBackCommand) {

                Display.getDisplay(this).setCurrent(pbx_List);

            } else if (thCmd == pinCodeBackCommand
                       || thCmd == AutoAccessBackCommand
                       || thCmd == AutoAccessBackNOPrefixCommand) {

                Display.getDisplay(this).setCurrent(pbx_List_type);

            } else if (thCmd == debugListLogOutCommand) {

                Display.getDisplay(this).setCurrent(alertLogOutDebug, pbx_List);

            } else if (thCmd == countryBackCommand) {

                Display.getDisplay(this).setCurrent(language_List);

            } else if (thCmd == loginGroupBackCommand
                       || thCmd == logoffGroupBackCommand) {

                Display.getDisplay(this).setCurrent(groupList);

            } else if (thCmd == atExtBackCommand
                       || thCmd == backAtBackCommand
                       || thCmd == outBackCommand) {

                Display.getDisplay(this).setCurrent(getAbsentList());
            }

            else if (thCmd == confirmOffYESCommand) {

                Mexoff();
                String close_mexoff = "e" + ",";

                sendMexOnOffMessage(close_mexoff);

                System.out.println("MEXOFF >>>> " + close_mexoff);

            }

            else if (thCmd == confirmOnYESCommand) {

                Display.getDisplay(this).setCurrent(getMainList());

                setCall();

                System.out.println("MEXON >>>> ");

            }

            /* ------- Exit - Commands --------------------- */

            else if (thCmd == confirmExitYESCommand) {

                notifyDestroyed();

            } else if (thCmd == mainListExitCommand) {

                Display.getDisplay(this).setCurrent(getAlertExit());

            }
        } catch (Exception ex) {
        }

    }

    /* **************************************************************************************************** */
    /* ===== Övriga kontroll-metoder som bör ligga i huvudklassen ========= */

    public void setAbsentStatusString(String s) {

        String presenceName = s;

        DataBase_RMS rms = null;
        try {
            rms = new DataBase_RMS();
        } catch (IOException ex) {
        } catch (RecordStoreNotOpenException ex) {
        } catch (InvalidRecordIDException ex) {
        } catch (RecordStoreException ex) {
        }
        Date_Time date = null;
        try {
            date = new Date_Time();
        } catch (InvalidRecordIDException ex1) {
        } catch (RecordStoreNotOpenException ex1) {
        } catch (RecordStoreException ex1) {
        } catch (IOException ex1) {
        }

        String minut = date.getAbsentMinute();
        String hour = date.getAbsentHour();
        int intYear = date.getYear();
        String year = Integer.toString(intYear);
        int intMounth = date.getMonth();
        String mounth = Integer.toString(intMounth);
        int intDay = date.getDay();
        String day = Integer.toString(intDay);

        String setAbsentStatusTime = presenceName + ":" + " " + hour + "." +
                                     minut + " " + year + "." + mounth + "." +
                                     day;
        System.out.println("Set STATUS >> " + setAbsentStatusTime);

        rms.setAbsentStatus(setAbsentStatusTime);

        date = null;
        rms = null;
    }

    public void Mexon() {

        DataBase_RMS rms = null;
        try {
            rms = new DataBase_RMS();
        } catch (IOException ex) {
        } catch (RecordStoreNotOpenException ex) {
        } catch (InvalidRecordIDException ex) {
        } catch (RecordStoreException ex) {
        }
        rms.setMexONOFF("1");
        rms = null;
        Display.getDisplay(this).setCurrent(getMainList());

    }

    public void Mexoff() {

        DataBase_RMS rms = null;
        try {
            rms = new DataBase_RMS();
        } catch (IOException ex) {
        } catch (RecordStoreNotOpenException ex) {
        } catch (InvalidRecordIDException ex) {
        } catch (RecordStoreException ex) {
        }
        rms.setMexONOFF("0");
        rms = null;
        Display.getDisplay(this).setCurrent(getMainList());

    }

    public void setCall() throws IOException, RecordStoreNotOpenException,
            InvalidRecordIDException, RecordStoreException {

        MDataStore.DataBase_RMS rms = new DataBase_RMS();
        this.pbx_ID = rms.getPbxID();
        this.pbx_type = rms.getPBXType();
        this.lineAccess_PBX = rms.getAccessNumber();

        // Pinkod Panasonic
        if (pbx_ID.equals("1") && pbx_type.equals("5")) {

            CallPINcodePanasonic();
        }

        // AutoAccess med linjePrefix
        else if (pbx_type.equals("3") && !this.lineAccess_PBX.equals("NONE")) {

            CallAutoAccess();

            // AutoAccess OCH med tom linjeprefix
        } else if (pbx_type.equals("3") && this.lineAccess_PBX.equals("NONE")) {

            CallNoLinjePrefix();

        }

        rms = null;

    }

    // --- AutoAccess utan linjeprefix --> '2'
    public void CallNoLinjePrefix() {

        Mexon();

        Thread noPrefix = new Thread() {

            public void run() {

                try {
                    String start_mexon_01 = "s," +
                                            switchBoardNumber_PBX + "," +
                                            "" + ","
                                            + countryCode_PBX + "," +
                                            voiceMailOperator_PBX + ",";

                    sendMexOnOffMessage(start_mexon_01);

                    System.out.println("Utan LinjePrefix MexON >> " +
                                       start_mexon_01);

                } catch (Exception ex) {
                }

            }
        };
        noPrefix.start();

    }

    // --- AutoAccess --> '3'
    public void CallAutoAccess() {

        Mexon();

        Thread autoAccess = new Thread() {

            public void run() {

                try {
                    String start_mexon_01 = "s," +
                                            switchBoardNumber_PBX + "," +
                                            lineAccess_PBX + ","
                                            + countryCode_PBX + "," +
                                            voiceMailOperator_PBX + ",";

                    sendMexOnOffMessage(start_mexon_01);

                    System.out.println("AutoAccess MexON >> " + start_mexon_01);

                } catch (Exception ex) {
                }

            }
        };
        autoAccess.start();

    }

    // --- PINKOD - Access Panasonic --> '5'
    public void CallPINcodePanasonic() {

        Mexon();

        Thread pansonicPIN = new Thread() {

            public void run() {

                try {
                    String start_mexon_01 = "s," +
                                            switchBoardNumber_PBX + "," +
                                            "*47" + extensionNumber_PBX +
                                            pinCodeNumber_PBX +
                                            lineAccess_PBX + ","
                                            + countryCode_PBX + "," +
                                            voiceMailOperator_PBX + ",";

                    sendMexOnOffMessage(start_mexon_01);

                    System.out.println("Panasonic PINcode MexON >> " +
                                       start_mexon_01);

                } catch (Exception ex) {
                }

            }
        };
        pansonicPIN.start();

    }


    // --- skickar in värde till sendMexOnOff()
    public void sendMexOnOffMessage(String message) {
        this.request = message;
        new Thread() {
            public void run() {
                sendMexOnOff();
            }
        }.start();
    }

    // --- metoden skickar in om mex är on | off
    public void sendMexOnOff() {
        try {
            StreamConnection conn = (StreamConnection) Connector.open(url);
            OutputStream out = conn.openOutputStream();
            byte[] buf = request.getBytes();
            out.write(buf, 0, buf.length);
            out.flush();
            out.close();

            byte[] data = new byte[256];
            InputStream in = conn.openInputStream();
            int actualLength = in.read(data);
            String response = new String(data, 0, actualLength);
            setAlertMEXONOFF(response);
            in.close();
            conn.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();

        }
    }

    // --- metoden ger olika Alert's, beroende på vilket värde som skickas från servern
    public void setAlertMEXONOFF(String resp) {

        boolean exist = false;

        this.ResponceMessage = resp;

        String controll = this.ResponceMessage.substring(0, 2);
        /*
                 if (controll.equals("sN")) { // Redan på
            exist = true;
            String setStringResponse = alertMessageMexAlreadyON_DEF;

            setStringResponse.length();

            alertMexAlreadyONOFF.setString(setStringResponse);

            Display.getDisplay(this).setCurrent(alertMexAlreadyONOFF);

                 }

                 if (controll.equals("eN")) { // Redan avstängd
            exist = true;
            String setStringResponse = alertMessageMexAlreadyOFF_DEF;

            setStringResponse.length();

            alertMexAlreadyONOFF.setString(setStringResponse);

            Display.getDisplay(this).setCurrent(alertMexAlreadyONOFF);

                 }*/

        if (controll.equals("sO")) { // Mex on
            exist = true;
            //alertON.setString("Vill du sätta på MEX?"/*alertMessageMEXOn_DEF*/);

            //Display.getDisplay(this).setCurrent(alertON);

        }

        if (controll.equals("eO")) { // Mex off
            exist = true;
            //alertOFF.setString("Vill du stänga av MEX?"/*alertMessageMEXOff_DEF*/);

            //Display.getDisplay(this).setCurrent(alertOFF);


        }
        if (controll.equals("fO")) { // Mex on, stänger prg
            exist = true;
            /* alertExit.setString(
                     programExitON_DEF);

             Display.getDisplay(this).setCurrent(alertExit);*/

        }

        if (controll.equals("fF")) { // Mex off, stänger prg
            exist = true;
            /*alertExit.setString(
                    programExitOFF_DEF);

                         Display.getDisplay(this).setCurrent(alertExit);*/

        }

        if (controll.equals("dO")) { // Debug på
            exist = true;
            String setStringResponse = "Debug on";

            setStringResponse.length();

            alertDebugONOFF.setString(setStringResponse);

            Display.getDisplay(this).setCurrent(alertDebugONOFF);

        }

        if (controll.equals("dN")) { // Debug på (fel)
            exist = true;
            String setStringResponse = "Debug on error";

            setStringResponse.length();

            alertDebugONOFF.setString(setStringResponse);

            Display.getDisplay(this).setCurrent(alertDebugONOFF);

        }

        if (controll.equals("pO")) { // Debug av
            exist = true;
            String setStringResponse = "Debug off";

            setStringResponse.length();

            alertDebugONOFF.setString(setStringResponse);

            Display.getDisplay(this).setCurrent(alertDebugONOFF);

        }

        if (controll.equals("pN")) { // Debug av (fel)
            exist = true;
            String setStringResponse = "Debug off error";

            setStringResponse.length();

            alertDebugONOFF.setString(setStringResponse);

            Display.getDisplay(this).setCurrent(alertDebugONOFF);

        }

        if (!exist) { // boolean

            String setStringResponse = resp;

            setStringResponse.length();

            alertSendOKNOK.setString(setStringResponse);

            Display.getDisplay(this).setCurrent(alertSendOKNOK);

        }

    }


    // --- metoden kontrollerar datum för demo-licenser
    public void ControllDateTime() throws InvalidRecordIDException,
            RecordStoreNotOpenException, RecordStoreException, IOException {

        MModel.Date_Time dateTime = new Date_Time();

        String s1 = setDay_30DAY; // Dag (30 dagar framåt i tiden)
        String s2 = setMounth_30DAY; // Månad (30 dagar framåt i tiden)
        String s3 = setYear_30DAY; // År (30 dagar framåt i tiden)
        String s4 = setYear_TODAY; // År (dagens datum)
        String s5 = setMounth_TODAY; // Månad (dagens datum)
        String s6 = setDay_TODAY; // Dag (dagens datum)

        dateTime.controllDate(s1, s2, s3, s4, s5, s6);
        this.ViewDateString = setDay_30DAY + " " + setMounthName_30DAY + " " +
                              setYear_30DAY;
        String licensStart = setDay_TODAY + " " + setMounthNameToday + " " +
                             setYear_TODAY;

        System.out.println("Licensen Startdatum >> " + licensStart);
        System.out.println("Licensen gäller till >> " + ViewDateString);
        /*
                 try {
            if (CheckTwo.equals("2")) {

                System.out.println("Licensen har gått ut idag den >> " +
                                   ViewDateString);
                notifyDestroyed();
                MModel.ConnectTCPIP_Socket tcpip = new ConnectTCPIP_Socket();
                tcpip.sendLCloseSRV();
                tcpip = null;

            }
                 } catch (Exception ex) {
                 }*/
        System.out.println("VÄRDET PLATS getTWO(); >> " + CheckTwo);
        dateTime = null;
    }


    /* ==================== SERVER Debug ======================================== */

    private String URLEncode(String s) {

        StringBuffer sbuf = new StringBuffer();
        int ch;
        for (int i = 0; i < s.length(); i++) {
            ch = s.charAt(i);
            switch (ch) {
            case ' ': {
                sbuf.append("+");
                break;
            }
            case '!': {
                sbuf.append("%21");
                break;
            }
            case '*': {
                sbuf.append("%2A");
                break;
            }
            case '\'': {
                sbuf.append("%27");
                break;
            }
            case '(': {
                sbuf.append("%28");
                break;
            }
            case ')': {
                sbuf.append("%29");
                break;
            }
            case ';': {
                sbuf.append("%3B");
                break;
            }
            case ':': {
                sbuf.append("%3A");
                break;
            }
            case '@': {
                sbuf.append("%40");
                break;
            }
            case '&': {
                sbuf.append("%26");
                break;
            }
            case '=': {
                sbuf.append("%3D");
                break;
            }
            case '+': {
                sbuf.append("%2B");
                break;
            }
            case '$': {
                sbuf.append("%24");
                break;
            }
            case ',': {
                sbuf.append("%2C");
                break;
            }
            case '/': {
                sbuf.append("%2F");
                break;
            }
            case '?': {
                sbuf.append("%3F");
                break;
            }
            case '%': {
                sbuf.append("%25");
                break;
            }
            case '#': {
                sbuf.append("%23");
                break;
            }
            case '[': {
                sbuf.append("%5B");
                break;
            }
            case ']': {
                sbuf.append("%5D");
                break;
            }
            default:
                sbuf.append((char) ch);
            }
        }
        return sbuf.toString();
    }

    public void sendLogdata() {

        logdata = "";
        sendMessageInt("k,IMEI,", IMEIDATA);
        sendMessageInt("i,", CONFDATA);
        sendMessageInt("u", LOGSIZE);

        int bufsize = 256;
        for (icount = 0; icount < logfilesize; icount = icount + bufsize) {
            logrequest = "j," + icount + ",";
            sendMessageInt(logrequest, LOGDATA);
        }

        sendLogdataExt();
    }

    public void sendRequestInt(String message, int what) {
        this.request = message;
        this.requestwhat = what;
        new Thread() {
            public void run() {
                sendMessageInt(request, requestwhat);
            }
        }.start();
    }


    public void sendMessageInt(String message, int what) {
        try {
            StreamConnection conn = (StreamConnection) Connector.open(inturl);
            OutputStream out = conn.openOutputStream();
            byte[] buf = message.getBytes();
            out.write(buf, 0, buf.length);
            out.flush();
            out.close();

            byte[] data = new byte[4096];
            InputStream in = conn.openInputStream();
            int actualLength = in.read(data);
            String response = new String(data, 0, actualLength);
            switch (what) {
            case CONFDATA:
                confdata = response;
                break;
            case LOGDATA:
                logdata = logdata + response;
                break;
            case IMEIDATA:
                imei = response;
                break;
            case LOGSIZE:
                logfilesize = Integer.parseInt(response);
                break;
            }
            in.close();
            conn.close();

        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public void sendLogdataExt() {
        try {
            HttpConnection conn = (HttpConnection)
                                  Connector.open(ext_url);
            String submitstring = "imei=" + URLEncode(imei) + "&confdata=" +
                                  URLEncode(confdata) + "&logdata=" +
                                  URLEncode(logdata) + "&logfilesize=" +
                                  logfilesize + "&icount=" + icount +
                                  "&Submit=Submit";

            byte[] data = submitstring.getBytes();

            conn.setRequestMethod(HttpConnection.POST);
            conn.setRequestProperty("User-Agent",
                                    "Profile/MIDP-1.0 Configuration/CLDC-1.0");
            conn.setRequestProperty("Content-Language", "en-US");
            conn.setRequestProperty("Content-Type",
                                    "application/x-www-form-urlencoded");
            OutputStream os = conn.openOutputStream();
            os.write(data);
            os.close();

            byte[] data2 = new byte[2048];
            InputStream in = conn.openInputStream();
            int actualLength = in.read(data2);
            String response = new String(data2, 0, actualLength);
            setAlertMEXONOFF(response);

            in.close();
            conn.close();

        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    // ***************** Splash-Screen ********************

    // --- Metoder
    public void startSplash() {

        try {

            if (!splashIsShown) {
                String sendName = prg_Name + " " + version_DEF;
                Displayable k = new SplashCanvas(sendName, ViewDateString);
                display.setCurrent(k);

            }

            doTimeConsumingInit();

            if (true) {
                // Game loop
            }

        } catch (Exception ex) {
        }

    }

    private void doTimeConsumingInit() {
        // Just mimic some lengthy initialization for 10 secs
        long endTime = System.currentTimeMillis() + 3000;
        while (System.currentTimeMillis() < endTime) {}

        DataBase_RMS rms = null;
        try {
            rms = new DataBase_RMS();
        } catch (IOException ex) {
        } catch (RecordStoreNotOpenException ex) {
        } catch (InvalidRecordIDException ex) {
        } catch (RecordStoreException ex) {
        }
        isInitialized = true;
        try {
            if (!rms.getTWO().equals("2")) {
                // init the game's main Displyable (here a Form mainList)

                Display.getDisplay(this).setCurrent(getMainList());

            }
        } catch (RecordStoreNotOpenException ex1) {
        } catch (InvalidRecordIDException ex1) {
        } catch (RecordStoreException ex1) {
        }
        try {
            if (rms.getTWO().equals("2")) {

                Display.getDisplay(this).setCurrent(alertExperinceLicense);

            }
        } catch (RecordStoreNotOpenException ex2) {
        } catch (InvalidRecordIDException ex2) {
        } catch (RecordStoreException ex2) {
        }

        rms = null;

    }

    // --- Två egna klasser
    public class SplashScreen implements Runnable {
        private SplashCanvas splashCanvas;

        public void run() {
            String sendName = prg_Name + " " + version_DEF;
            splashCanvas = new SplashCanvas(sendName, ViewDateString);
            display.setCurrent(splashCanvas);
            splashCanvas.repaint();
            splashCanvas.serviceRepaints();
            while (!isInitialized) {
                try {
                    Thread.yield();
                } catch (Exception e) {}
            }

        }

    }


    public class SplashCanvas extends Canvas {

        private String prg_Name, viewDateString;

        // Tar emot värden från huvudclassen i konstruktorn.
        public SplashCanvas(String name, String ViewDateString) {

            this.viewDateString = ViewDateString;
            this.prg_Name = name;

        }

        protected void paint(Graphics g) {
            int width = getWidth();
            int height = getHeight();

            // Create an Image the same size as the
            // Canvas.
            Image image = Image.createImage(width, height);
            Graphics imageGraphics = image.getGraphics();

            // Fill the background of the image black
            imageGraphics.setColor(0x000000);
            imageGraphics.fillRect(0, 0, width, height);

            // Draw a pattern of lines
            int count = 10;
            int yIncrement = height / count;
            int xIncrement = width / count;
            for (int i = 0, x = xIncrement, y = 0; i < count; i++) {
                imageGraphics.setColor(0xC0 + ((128 + 10 * i) << 8) +
                                       ((128 + 10 * i) << 16));
                imageGraphics.drawLine(0, y, x, height);
                y += yIncrement;
                x += xIncrement;
            }

            // Add some text
            imageGraphics.setFont(Font.getFont(Font.FACE_PROPORTIONAL, 0,
                                               Font.SIZE_SMALL));
            imageGraphics.setColor(0xffff00);
            imageGraphics.drawString(prg_Name, width / 2, 15,
                                     Graphics.TOP | Graphics.HCENTER);

            try {
                Image image1 = Image.createImage("/mobisma_icon/mexa.png");
                imageGraphics.drawImage(image1, width / 2, 50,
                                        Graphics.TOP | Graphics.HCENTER);
            } catch (IOException ex) {
            }

            imageGraphics.drawString(viewDateString, width / 2, 100,
                                     Graphics.TOP | Graphics.HCENTER);

            imageGraphics.setColor(0xffffff);
            imageGraphics.drawString("mobisma AB", width / 2, 120,
                                     Graphics.TOP | Graphics.HCENTER);
            imageGraphics.drawString("All Rights Reserved © | 2008", width / 2,
                                     140, Graphics.TOP | Graphics.HCENTER);

            // Copy the Image to the screen
            g.drawImage(image, 0, 0, Graphics.TOP | Graphics.LEFT);

            splashIsShown = true;
        }


    }


}
