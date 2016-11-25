package korr


/**
 * This domain has an association with another domain, called Owned, but we want to add 
 * an extra property (or two, or three...) to the association.
 * So not only does Owner have some Owned state but we want to further maintain some state
 * between the two domains in the join table.
 * The 'hasMany' property creates a join table between Owner and Owned but how do you add
 * extra properties to that join table, say to maintain some 'state'?
 * You create a new domain to model the join table (which will hold your state) and inside the Owner
 * you expliclty rename the join table - overriding GORM default naming convention 
 *
 */
class Parent {

    String id = UUID.randomUUID().toString()

    String name
    String someProperty
    
    /** so this represents our association between Parent and Child domains but maintains whatever state we want */
    static hasMany = [parentChildLinks: ParentChildLink]

    static constraints = {
        name nullable: false, unique: true, blank: false, minSize: 1, maxSize: 240
        someProperty nullable: false, blank: false, minSize: 1, maxSize: 100
        parentChildLinks nullable: false
    }

    static mapping = {
        id generator: "assigned"
        // we need to christen the join table 'cos GORM will use convention over configuration & go nuts with the table name
        parentChildLinks joinTable: [name: 'parent_child_link']
    }
}
