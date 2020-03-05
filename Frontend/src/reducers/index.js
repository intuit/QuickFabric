import { combineReducers } from 'redux'
import { user } from './user'
import { allEmrHealthData } from './emrhealth'
import { emrCostData } from './emrcost'
import { emrMetadataData } from './emrmanagement'
import { adminMetadata } from './admin'
import { profileMetadata } from './profile'

const rootReducer = combineReducers({
  user,
  allEmrHealthData,
  emrCostData,
  emrMetadataData,
  adminMetadata,
  profileMetadata
})

export default rootReducer