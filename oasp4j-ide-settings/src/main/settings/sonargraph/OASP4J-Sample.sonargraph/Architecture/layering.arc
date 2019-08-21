// Technical Architecture of an OASP application
// - defines an artifact per layer with interfaces for the sub packages
// - all artifacts and interfaces optional, depends on the component what is used, nothing mandatory

// service layer
exposed optional artifact service
{
    
    include "**/service/**"
    
    // default interface: api package
    optional interface default {
        
        include "**/api/**"
        
    }
    
    // service configuration: can instantiate common implementation artifacts
    optional artifact configuration {
        
        include "**/impl/config/**"
        
        connect to common.impl
        
    }
    
    // service implementation (can be instantiated via configurations)
	optional interface impl {
        
        include "**/impl/**"
        
    }
    
    // only depend on logic and allow common extensions
    connect to logic, common.base
}

// batch layer
optional artifact batch {
    
    include "**/batch/**"
    
    // only depend on logic, allow common extensions and direct DAO access
    connect to logic, dataaccess, common.base
    
}

// logic layer 
exposed optional artifact logic
{
    include "**/logic/**"
    // s. comment in common artifact
    exclude "**/to/**"
    
    // default interface: api package
    optional interface default {
        
        include "**/api/**"
        
    }
    
    // base package providing extension points for implementation in upper layers or components
    optional interface base {
        
        include "**/base/**"
        
        include dependency-types IMPLEMENTS, EXTENDS, USES
        
    }
    
    // logic configuration: can instantiate common implementation artifacts
    optional artifact configuration {
        
        include "**/impl/config/**"
        
        connect to common.impl
        
    }
    
    // only depend on dataaccess (including DAOs!) and common extension points
    connect to dataaccess, dataaccess.dao, common.base
    
    
}

// dataaccess layer
exposed optional artifact dataaccess
{
    include "**/dataaccess/**"
    
    // default interface: api package
    optional interface default {
        
        include "**/api/*"
        
    }
    
    // explicitly defining DAO interface only to be used by own components logic layer
    optional interface dao {
        
        include "**/dao/**"
        
    }
    
    // base package providing extension points for implementation in upper layers or components
    optional interface base {
        
        include "**/base/**"
        
        include dependency-types IMPLEMENTS, EXTENDS, USES
        
    }
    
    // only allow common extension points
    connect to common.base
    
}

// common layer
exposed public optional artifact common {
    
    include "**/common/**"
    // transport objects can be used throughout the layers
    include "**/to/**"
    
    // default interface: api package + transport objects
    optional interface default {
        include "**/api/**"
    	include "**/to/**"
        include "**/enums/**"
    }
    
    // base package providing extension points for implementation in upper layers or components
    optional interface base {
        include "**/base/**"
        include dependency-types IMPLEMENTS, EXTENDS, USES
    }
    
    // implementation which can be used in configuration of upper layers
    optional interface impl {
        include "**/impl/**"
        include dependency-types NEW, CALL, USES
    }
}


deprecated unrestricted artifact UnassignedToLayer {
    
    include "**"
    
}
