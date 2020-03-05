export const converStrToBoolean = (val) => {
    if(typeof val === 'string') {
        if(val === 'False' || val === 'false') {
            return false
        } else if(val === 'True' || val === 'true') {
            return true
        }
    } 
  } 
  