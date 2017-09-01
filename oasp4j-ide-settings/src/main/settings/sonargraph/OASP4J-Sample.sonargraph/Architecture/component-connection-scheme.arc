
// Connection scheme defining how components of a business architecture may depend on each other regarding the technical architecture
connection-scheme C2C {
    
    // services from one component may access other services, use cases and of course common artifacts
    connect service to target.service, target.logic, target.common, target.common.base
    
    // service configuration may instantiate other service implementations
    connect service.configuration to target.service.impl
    
    // use cases may access other use cases, logic extension points and of course common artifacts
    connect logic to target.logic, target.logic.base, target.common, target.common.base
    
    // data access may access other dataaccess entities (but not DAOs!), dataaccess extenspoint points and of course common artifacts
    connect dataaccess to target.dataaccess, target.dataaccess.base, target.common, target.common.base
    
    // common artifacts may access other common artifacts and their extension points
    connect common to target.common, target.common.base
    
}
