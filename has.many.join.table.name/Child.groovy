package korr


/**
 * This represents the 'owned' side of the 'hasMany' mapping with {@link Parent}
 *
 */
class Child {

    String id = UUID.randomUUID().toString()

    String whatever

    static constraints = {
        whatever nullable: false, blank: false        
    }

    static mapping = {
        id generator: "assigned"
    }
}