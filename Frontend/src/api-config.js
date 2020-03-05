let baseURL,baseProdURL, dccURL

const hostname = window && window.location && window.location.hostname

if (hostname === 'virtual-steward.datacatalog.a.intuit.com') {
  baseURL = 'https://virtual-steward.datacatalog.a.intuit.com/DataCatalog_Service/services'
} else {
  baseURL = 'http://localhost:8080/DataCateLog'
}


 //baseURL = 'http://localhost:8082/DataCatalog_Service/services'
 //baseURL = 'http://localhost:8080/DCC/services'
 //baseURL = "https://dcc-preprod.sbseg.a.intuit.com/DCC_Services"
   baseURL = 'http://'+hostname+'/quickfabric/services'
 //baseURL = 'https://virtual-steward.quickdata-prod.a.intuit.com/DataCatalog_Service/services'
 //dccURL = 'http://172.26.156.83:8080/DCC/services/EMRClusterManagementService'

export default baseURL
