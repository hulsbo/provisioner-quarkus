package io.hulsbo.model;

import com.fasterxml.jackson.databind.ser.Serializers;
import io.hulsbo.util.model.SafeID;
import io.hulsbo.util.model.baseclass.ChildWrapper;

import java.lang.reflect.InaccessibleObjectException;
import java.util.*;
import java.util.stream.Collectors;

public class Manager {
    static final Map<SafeID, BaseClass> baseClassIndex = new HashMap<>();
    static final Map<SafeID, CrewMember> crewMemberIndex = new HashMap<>();

    static void register(SafeID id, BaseClass baseClass) {
        baseClassIndex.put(id, baseClass);
    }
    static void register(SafeID id, CrewMember crewMember) {
        crewMemberIndex.put(id, crewMember);
    }
    public static Set<BaseClass> findParents(SafeID id) {
        Set<SafeID> keys = baseClassIndex.keySet();
        Set<BaseClass> parents = new HashSet<>();

        for (SafeID key : keys) {
            ChildWrapper elementOwnChild = baseClassIndex.get(key).childMap.get(id);

            if (elementOwnChild != null) {
                parents.add(baseClassIndex.get(key));
            }
        }
        return parents;
    }


//    TODO: Add parameter stating what Class is expected, or make getter for each one.
    public static BaseClass getBaseClass(SafeID id) {
        return baseClassIndex.get(id);
    }

    public static CrewMember getCrewMember(SafeID id) {
        return crewMemberIndex.get(id);
    }


    public static String removeBaseClassObject(SafeID id) {

        BaseClass baseClass = baseClassIndex.remove(id);

        if (baseClass != null) {
            return baseClass.getClass().getSimpleName() + " \"" +  baseClass.getName() + "\" " + " was successfully removed from index.";
        } else {
            throw new InaccessibleObjectException("Object with id " + id + " could not be found in baseClassIndex. Already deleted?");
        }
    }

    static String removeCrewMember(SafeID id) {


        CrewMember crewMember = crewMemberIndex.remove(id);

        if (crewMember != null) {
            return crewMember.getName() + " was successfully removed from index.";
        } else {
            throw new InaccessibleObjectException("CrewMember with id " + id + " could not be found in crewMemberIndex. Already deleted?");
        }
    }

    public static List<Adventure> getAllAdventures() {
        return baseClassIndex.values().stream()
                .filter(obj -> obj instanceof Adventure)
                .map(obj -> (Adventure) obj)
                .collect(Collectors.toList());
    }

    public static <T extends BaseClass> List<T> getAllOf(Class<T> subclass) {
        return baseClassIndex.values().stream()
                .filter(subclass::isInstance)
                .map(subclass::cast)
                .collect(Collectors.toList());
    }


}

