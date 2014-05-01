# Scripts are passed the ff. variables:
#
# data - the Map instance representing a single 'record' of data.
# type - the Type name
# config - the groovy.util.ConfigObject
# log - a org.apache.log4j.Logger
# scriptPath - the path of this script
# environment - the current environment

# And must set a global 'data' Map instance. The keys must match the field names of the Type. Scripts setting 'data' to null invalidates the record.
# The script can also set a 'message' global variable.

if (data):
    if (log.isDebugEnabled()):
        log.debug("Jython script checking for missing fields...")
    reqFields = config["types"][type]["required"]
    for field in reqFields:
        if not data[field] :
            message = "Required field is missing: '%s' for data: %s" % (field, data)
            data = None
            break
else:
    if (log.isDebugEnabled()):
        log.debug("No data to check...")
    message = "No data to check."
                
if (data is not None) :
    message = "Require fields have data."
    
if (log.isDebugEnabled()): 
    log.debug(message)

        
            
            
        