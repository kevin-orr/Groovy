package korr

/**
 * Represents the join table for the association between {@link Parent} and {@link Child}
 *
 * However, we can now maintain whatever state we want to keep and associate between the
 * Parent and Child domain objects.
 *
 */
class ParentChildLink {

    String id = UUID.randomUUID().toString()

    Parent parent
    Child child
    boolean likesCats
    boolean likesDogs

    static mapping = {
        id generator: "assigned"
    }
}
