package universe;

import com.sun.source.tree.BinaryTree;
import com.sun.source.tree.IdentifierTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.TypeCastTree;

import org.checkerframework.common.basetype.BaseAnnotatedTypeFactory;
import org.checkerframework.common.basetype.BaseTypeChecker;
import org.checkerframework.framework.type.AnnotatedTypeFactory;
import org.checkerframework.framework.type.AnnotatedTypeMirror;
import org.checkerframework.framework.type.AnnotatedTypeMirror.AnnotatedDeclaredType;
import org.checkerframework.framework.type.ViewpointAdapter;
import org.checkerframework.framework.type.treeannotator.ListTreeAnnotator;
import org.checkerframework.framework.type.treeannotator.LiteralTreeAnnotator;
import org.checkerframework.framework.type.treeannotator.PropagationTreeAnnotator;
import org.checkerframework.framework.type.treeannotator.TreeAnnotator;
import org.checkerframework.framework.type.typeannotator.DefaultForTypeAnnotator;
import org.checkerframework.javacutil.TreeUtils;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;

import universe.qual.Any;
import universe.qual.Bottom;
import universe.qual.Lost;
import universe.qual.Payload;
import universe.qual.Peer;
import universe.qual.Rep;
import universe.qual.Self;
import universe.qual.Dom;

/**
 * Apply viewpoint adaptation and add implicit annotations to "this" and "super".
 *
 * @author wmdietl
 */
public class UniverseAnnotatedTypeFactory extends BaseAnnotatedTypeFactory {

    public UniverseAnnotatedTypeFactory(BaseTypeChecker checker) {
        // always use dataflow analysis, since we do not do any inference
        super(checker, true);
        this.postInit();
    }

    /** The type of "this" is always "self". */
    @Override
    public AnnotatedDeclaredType getSelfType(Tree tree) {
        var type = super.getSelfType(tree);
        if (type != null) type.replaceAnnotation(UniverseAnnotationMirrorHolder.SELF);
        return type;
    }

    @Override
    protected ViewpointAdapter createViewpointAdapter() {
        return new UniverseViewpointAdapter(this);
    }

    /**
     * Create our own TreeAnnotator.
     *
     * @return the new TreeAnnotator.
     */
    @Override
    protected TreeAnnotator createTreeAnnotator() {
        return new ListTreeAnnotator(
                new UniversePropagationTreeAnnotator(this),
                new LiteralTreeAnnotator(this),
                new UniverseTreeAnnotator());
    }

    @Override
    protected Set<Class<? extends Annotation>> createSupportedTypeQualifiers() {
        var annotations =
                Arrays.asList(
                        Any.class,
                        Lost.class,
                        Peer.class,
                        Rep.class,
                        Self.class,
                        Bottom.class,
                        Payload.class,
                        Dom.class);

        return Collections.unmodifiableSet(new HashSet<>(annotations));
    }

    @Override
    public void addComputedTypeAnnotations(Element elt, AnnotatedTypeMirror type) {
        UniverseTypeUtil.defaultConstructorReturnToSelf(elt, type);
        super.addComputedTypeAnnotations(elt, type);
    }

    /** Replace annotation of extends or implements clause with SELF in Universe. */
    @Override
    public AnnotatedTypeMirror getTypeOfExtendsImplements(Tree clause) {
        var s = super.getTypeOfExtendsImplements(clause);
        s.replaceAnnotation(UniverseAnnotationMirrorHolder.SELF);
        return s;
    }

    /** Currently only needed to add the "self" modifier to "super". */
    private class UniverseTreeAnnotator extends TreeAnnotator {

        private UniverseTreeAnnotator() {
            super(UniverseAnnotatedTypeFactory.this);
        }

        /**
         * Add @Self to "super" identifier.
         *
         * @param node the identifier node.
         * @param p the AnnotatedTypeMirror to modify.
         */
        @Override
        public Void visitIdentifier(IdentifierTree node, AnnotatedTypeMirror p) {
            assert node != null;
            assert p != null;

            // There's no "super" kind, so make a string comparison.
            if (node.getName().contentEquals("super")) {
                p.replaceAnnotation(UniverseAnnotationMirrorHolder.SELF);
            }

            return super.visitIdentifier(node, p);
        }

        @Override
        public Void visitMethod(MethodTree node, AnnotatedTypeMirror p) {
            ExecutableElement executableElement = TreeUtils.elementFromDeclaration(node);

            UniverseTypeUtil.defaultConstructorReturnToSelf(executableElement, p);
            return super.visitMethod(node, p);
        }
    }

    private static class UniversePropagationTreeAnnotator extends PropagationTreeAnnotator {
        /**
         * Creates a {@link DefaultForTypeAnnotator} from the given checker, using that checker's
         * type hierarchy.
         *
         * @param atypeFactory type factory to use
         */
        public UniversePropagationTreeAnnotator(AnnotatedTypeFactory atypeFactory) {
            super(atypeFactory);
        }

        /**
         * Add immutable to the result type of a binary operation if the result type is implicitly
         * immutable
         */
        @Override
        public Void visitBinary(BinaryTree node, AnnotatedTypeMirror type) {
            applyImmutableIfImplicitlyBottom(
                    type); // Usually there isn't existing annotation on binary trees, but to be
            // safe, run it first
            super.visitBinary(node, type);
            return null;
        }

        /** Add immutable to the result type of a cast if the result type is implicitly immutable */
        @Override
        public Void visitTypeCast(TypeCastTree node, AnnotatedTypeMirror type) {
            applyImmutableIfImplicitlyBottom(
                    type); // Must run before calling super method to respect existing annotation
            return super.visitTypeCast(node, type);
        }

        /**
         * Because TreeAnnotator runs before DefaultForTypeAnnotator, implicitly immutable types are
         * not guaranteed to always have immutable annotation. If this happens, we manually add
         * immutable to type. We use addMissingAnnotations because we want to respect existing
         * annotation on type
         */
        private void applyImmutableIfImplicitlyBottom(AnnotatedTypeMirror type) {
            if (!UniverseTypeUtil.isImplicitlyBottomType(type)) return;

            var bottomList = Arrays.asList(UniverseAnnotationMirrorHolder.BOTTOM);
            type.addMissingAnnotations(new HashSet<>(bottomList));
        }
    }
}
