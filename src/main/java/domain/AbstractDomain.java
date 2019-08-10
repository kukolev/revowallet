package domain;

public abstract class AbstractDomain {

    private Long id;

    public void setId(Long id) {
        this.id = id;
    }
    public Long getId() {
        return id;
    }
}
