package org.padler.thydbadmin;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.UUID;

@Entity
@Table(name = "uuid_table")
public class TableWithUUID implements Serializable {

    @Id
    @Column(name = "uuid", unique = true)
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    private UUID uuid;

}
