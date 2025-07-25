/*
 * creedengo - Java language - Provides rules to reduce the environmental footprint of your Java programs
 * Copyright © 2024 Green Code Initiative (https://green-code-initiative.org/)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.greencodeinitiative.creedengo.java.checks;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.sonar.check.Rule;
import org.sonar.plugins.java.api.IssuableSubscriptionVisitor;
import org.sonar.plugins.java.api.semantic.MethodMatchers;
import org.sonar.plugins.java.api.tree.BaseTreeVisitor;
import org.sonar.plugins.java.api.tree.ClassTree;
import org.sonar.plugins.java.api.tree.CompilationUnitTree;
import org.sonar.plugins.java.api.tree.ExpressionTree;
import org.sonar.plugins.java.api.tree.ForStatementTree;
import org.sonar.plugins.java.api.tree.IdentifierTree;
import org.sonar.plugins.java.api.tree.MethodInvocationTree;
import org.sonar.plugins.java.api.tree.PackageDeclarationTree;
import org.sonar.plugins.java.api.tree.Tree;
import org.sonarsource.analyzer.commons.annotations.DeprecatedRuleKey;

@Rule(key = "GCI69")
@DeprecatedRuleKey(repositoryKey = "ecocode-java", ruleKey = "EC69")
@DeprecatedRuleKey(repositoryKey = "greencodeinitiative-java", ruleKey = "S69")
public class NoFunctionCallWhenDeclaringForLoop extends IssuableSubscriptionVisitor {

    protected static final String MESSAGERULE = "Do not call a function when declaring a for-type loop";

    private static final String ITERATOR = "java.util.Iterator";
    private static final MethodMatchers ITERATOR_METHODS = MethodMatchers.create()
            .ofSubTypes(ITERATOR)
            .names("hasNext", "next")
            .withAnyParameters()
            .build();
    private static final String ENUMERATION = "java.util.Enumeration";
    private static final MethodMatchers ENUMERATION_METHODS = MethodMatchers.create()
            .ofSubTypes(ENUMERATION)
            .names("hasMoreElements", "nextElement")
            .withAnyParameters()
            .build();

    
    private static final Map<String, Collection<Integer>> linesWithIssuesByClass = new HashMap<>();

    @Override
    public List<Tree.Kind> nodesToVisit() {
        return Collections.singletonList(Tree.Kind.FOR_STATEMENT);
    }

    @Override
    public void visitNode(Tree tree) {
        ForStatementTree method = (ForStatementTree) tree;
        MethodInvocationInForStatementVisitor invocationMethodVisitor = new MethodInvocationInForStatementVisitor();
        ExpressionTree condition = method.condition();
        if (null != condition) {
            method.condition().accept(invocationMethodVisitor);
        }

        // update
        method.update().accept(invocationMethodVisitor);
    }

    private class MethodInvocationInForStatementVisitor extends BaseTreeVisitor {

        @Override
        public void visitMethodInvocation(MethodInvocationTree tree) {
            if (!lineAlreadyHasThisIssue(tree) && !isMethodAllowed(tree)) {
                report(tree);
                return;
            }
            super.visitMethodInvocation(tree);
        }

        private boolean isMethodAllowed(MethodInvocationTree tree) {
            return ITERATOR_METHODS.matches(tree) || ENUMERATION_METHODS.matches(tree);
        }

        private boolean lineAlreadyHasThisIssue(Tree tree) {
            if (tree.firstToken() != null) {
                final String classname = getFullyQualifiedNameOfClassOf(tree);
                final int line = tree.firstToken().range().start().line();

                return linesWithIssuesByClass.containsKey(classname)
                        && linesWithIssuesByClass.get(classname).contains(line);
            }

            return false;
        }

        private void report(Tree tree) {
            if (tree.firstToken() != null) {
                final String classname = getFullyQualifiedNameOfClassOf(tree);
                final int line = tree.firstToken().range().start().line();

                linesWithIssuesByClass.computeIfAbsent(classname, k -> new ArrayList<>());

                linesWithIssuesByClass.get(classname).add(line);
            }

            reportIssue(tree, MESSAGERULE);
        }

        private String getFullyQualifiedNameOfClassOf(Tree tree) {
            Tree parent = tree.parent();

            while (parent != null) {
                final Tree grandparent = parent.parent();

                if (parent.is(Tree.Kind.CLASS) && grandparent != null && grandparent.is(Tree.Kind.COMPILATION_UNIT)) {
                    final String packageName = getPackageName((CompilationUnitTree) grandparent);

                    return packageName.isEmpty() ? getClassName((ClassTree) parent)
                            : packageName + '.' + getClassName((ClassTree) parent);
                }

                parent = parent.parent();
            }

            return "";
        }

        private String getPackageName(CompilationUnitTree compilationUnitTree) {
            final PackageDeclarationTree packageDeclarationTree = compilationUnitTree.packageDeclaration();
            if (packageDeclarationTree == null) {
                return "";
            }

            return packageDeclarationTree.packageName().toString();
        }

        private String getClassName(ClassTree classTree) {
            final IdentifierTree simpleName = classTree.simpleName();
            return simpleName == null ? "" : simpleName.toString();
        }
    }

}
