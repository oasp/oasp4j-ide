// Business Architecture definition, which is specific to an application

// defining how components may interact:
apply "component-connection-scheme"

// new artifact sitting at the top
artifact Application {
    
    include "**/io/oasp/gastronomy/restaurant/application/**"
    
    artifact Bootstrap {
    	include "**/io/oasp/gastronomy/restaurant/application/*"
    }
    
    apply "layering"
    
    connect to SalesManagement, TableManagement, StaffManagement, OfferManagement, General using C2C
    
}

artifact SalesManagement {
 
    include "**/io/oasp/gastronomy/restaurant/salesmanagement/**"
    
    apply "layering"
    
    connect to General, OfferManagement, TableManagement using C2C
    
}

artifact TableManagement {
 
    include "**/io/oasp/gastronomy/restaurant/tablemanagement/**"
    
    apply "layering"
    
    connect to General, StaffManagement using C2C
    
}

artifact StaffManagement {
 
    include "**/io/oasp/gastronomy/restaurant/staffmanagement/**"
    
    apply "layering"
    
    connect to General using C2C
    
}

artifact OfferManagement {
 
    include "**/io/oasp/gastronomy/restaurant/offermanagement/**"
    
    apply "layering"
    
    connect to General using C2C
    
}

artifact General {
 
    include "**/io/oasp/gastronomy/restaurant/general/**"
    
    apply "layering"
    
}

deprecated unrestricted artifact UnassignedToComponent {
    
    include "ayp.*/**"
    
    apply "layering"
    
}

