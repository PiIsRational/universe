package universe;

import com.sun.source.util.TreePath;

import org.checkerframework.framework.qual.DefaultFor;
import org.checkerframework.framework.type.AnnotatedTypeMirror;
import org.checkerframework.javacutil.TreePathUtil;
import org.checkerframework.javacutil.TypesUtils;

import java.util.Arrays;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;

import universe.qual.Bottom;

public class UniverseTypeUtil {

    private static boolean isInTypesOfImplicitForOfBottom(AnnotatedTypeMirror atm) {
        var defaultFor = Bottom.class.getAnnotation(DefaultFor.class);
        assert defaultFor != null;
        assert defaultFor.typeKinds() != null;

        for (var typeKind : defaultFor.typeKinds()) {
            if (TypeKind.valueOf(typeKind.name()) == atm.getKind()) return true;
        }

        return false;
    }

    private static boolean isInTypeNamesOfImplicitForOfBottom(AnnotatedTypeMirror atm) {
        if (atm.getKind() != TypeKind.DECLARED) return false;

        var defaultFor = Bottom.class.getAnnotation(DefaultFor.class);

        assert defaultFor != null;
        assert defaultFor.types() != null;

        var typeNames = defaultFor.types();
        var fqn = TypesUtils.getQualifiedName((DeclaredType) atm.getUnderlyingType()).toString();

        for (int i = 0; i < typeNames.length; i++) {
            if (typeNames[i].getCanonicalName().toString().contentEquals(fqn)) {
                return true;
            }
        }

        return false;
    }

    public static boolean isImplicitlyBottomType(AnnotatedTypeMirror atm) {
        return isInTypesOfImplicitForOfBottom(atm) || isInTypeNamesOfImplicitForOfBottom(atm);
    }

    private static boolean isRepOnly(DeclaredType anno) {
        return anno.toString().equals(universe.qual.RepOnly.class.getName());
    }

    public static boolean isRepOnly(ExecutableElement executableElement) {
        var annots = executableElement.getAnnotationMirrors();

        for (var an : annots) {
            if (!isRepOnly(an.getAnnotationType())) continue;
            return true;
        }

        return false;
    }

    private static boolean isPure(DeclaredType anno) {
        return anno.toString().equals(universe.qual.Pure.class.getName())
                || anno.toString().equals(org.jmlspecs.annotation.Pure.class.getName());
    }

    public static boolean isPure(ExecutableElement executableElement) {
        var anns = executableElement.getAnnotationMirrors();

        for (var an : anns) {
            if (!isPure(an.getAnnotationType())) continue;
            return true;
        }

        return false;
    }

    public static boolean inStaticScope(TreePath treePath) {
        if (!TreePathUtil.isTreeInStaticScope(treePath)) return false;

        // Exclude case in which enclosing class is static
        var classTree = TreePathUtil.enclosingClass(treePath);
        return classTree == null || !classTree.getModifiers().getFlags().contains(Modifier.STATIC);
    }

    public static void defaultConstructorReturnToSelf(Element elt, AnnotatedTypeMirror type) {
        if (elt.getKind() != ElementKind.CONSTRUCTOR
                || !(type instanceof AnnotatedTypeMirror.AnnotatedExecutableType)) {
            return;
        }

        var selfList = Arrays.asList(UniverseAnnotationMirrorHolder.SELF);
        ((AnnotatedTypeMirror.AnnotatedExecutableType) type)
                .getReturnType()
                .addMissingAnnotations(selfList);
    }
}
