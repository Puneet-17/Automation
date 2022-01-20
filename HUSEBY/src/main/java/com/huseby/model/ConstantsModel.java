package com.huseby.model;

public class ConstantsModel {

    public final static String resource = "src/main/resources/";
    public final static String inputFile = resource + "/inputfiles";
    public final static String schemaFiles = resource + "/jsonschema";
    public final static String outputFile = resource + "/output";

    // Memos URI  details
    public static final String Root_URI = "v2/pet/";
    public static final String findByStatus = Root_URI + "findByStatus";

    // Content Type
    public static final String Application_JSON = "application/json";
    public static final String Application_XML = "application/xml";

    public enum FileName {
        PublicSchemaRelationshipCount("publicSchemaRelationshipCount"),
        MatchesSearch("matchesSearch"),
        AddAWSConfig("addAWSConfig"),
        AuditRulesById("auditRulesById"),
        EmptyRules("emptyRules"),
        EmptyAgentsAPI("emptyAgentsAPI"),
        EmptyAWS("emptyAWS"),
        EmptyServiceNow("emptyServiceNow"),
        EmptyDecomposition("emptyDecomposition"),
        EmptyGovernance("emptyGovernance"),
        OpenAgents("openAgents"),
        RegisteredAgents("registeredAgents"),
        AlreadyRegisteredAgents("alreadyRegisteredAgents"),
        StatusSuccess("statusSuccess"),
        GetAgents("getAgents"),
        DecompositionDatabase("decompositionDatabase"),
        AuditChanges("auditChanges"),
        AuditChangesAdded("auditChangesAdded"),
        DBCreate("DBCreate"),
        DBTestRemove("DBTestRemove"),
        Java("java"),
        Net("net"),
        CreateRule("createRule"),
        CreateRuleWithFilters("createRuleWithFilters"),
        MatchesByNodeID("nodeID"),
        SchemaAuditHistory("AuditHistory"),
        SchemaAuditCDOForId("AuditCDOForId"),
        SchemaAuditHistoryDates("AuditHistoryDates"),
        SchemaAuditHistoryIdsForDate("AuditHistoryIdsForDate"),
        SchemaAuditRuleCreate("AuditRuleCreate"),
        SchemaAuditRules("AuditRules"),
        SchemaAuditRuleEnable("auditRuleEnable"),
        SchemaAuditRuleDisable("auditRuleDisable"),
        SchemaDatabaseCreate("databaseCreate"),
        SchemaDatabaseRemove("databaseRemove"),
        SchemaDotNetCreate("dotNetCreate"),
        SchemaGlobalSearch("globalSearch"),
        SchemaNodeContextSearch("nodeContextSearch"),
        SchemaGlobalSearchCount("globalSearchCount"),
        SchemaJavaExeCreate("javaExeCreate"),
        SchemaMatches("matches"),
        SchemaNodeComparison("nodeComparison"),
        SchemaTestClassloader("testClassloader"),
        SchemaActuatorHealth("actuatorHealth"),
        SchemaTopLevelNodes("topLevelNodes"),
        SchemaTopLevelNodeForId("topLevelNodeForId"),
        SchemaActuatorInfo("actuatorInfo");

        private String value;

        private FileName(String value) {
            this.value = value;
        }

        public String toString() {
            return value;
        }
    }

}
