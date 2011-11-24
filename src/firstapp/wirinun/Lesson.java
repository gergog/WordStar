/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package firstapp.wirinun;

/**
 *
 * @author ETHGGY
 */
public class Lesson {
    private String name;
    private long id;
    private long parentId = 0;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getParentId() {
        return parentId;
    }

    public void setParentId(long parentId) {
        this.parentId = parentId;
    }
    
    
    
    
    
}
