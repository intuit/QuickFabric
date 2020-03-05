/**
 * Check whether Jira is enabled globally and for the account.
 */
export const checkJiraEnabled = (globalJira, accountJira) => {
    return (globalJira !== undefined && 
        globalJira.configValue === "true") && 
        (accountJira !== undefined &&
        accountJira.configValue === "true")
}