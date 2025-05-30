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

import org.junit.jupiter.api.Test;
import org.sonar.java.checks.verifier.CheckVerifier;

class AvoidGettingSizeCollectionInLoopTest {
    @Test
    void testBadForLoop() {
        CheckVerifier.newVerifier()
                .onFile("src/test/files/AvoidGettingSizeCollectionInForLoopBad.java")
                .withCheck(new AvoidGettingSizeCollectionInLoop())
                .verifyIssues();
    }

    @Test
    void testIgnoredForLoop() {
        CheckVerifier.newVerifier()
                .onFile("src/test/files/AvoidGettingSizeCollectionInForLoopIgnored.java")
                .withCheck(new AvoidGettingSizeCollectionInLoop())
                .verifyNoIssues();
    }

    @Test
    void testGoodForLoop() {
        CheckVerifier.newVerifier()
                .onFile("src/test/files/AvoidGettingSizeCollectionInForLoopGood.java")
                .withCheck(new AvoidGettingSizeCollectionInLoop())
                .verifyNoIssues();
    }

    @Test
    void testBadWhileFoop() {
        CheckVerifier.newVerifier()
                .onFile("src/test/files/AvoidGettingSizeCollectionInWhileLoopBad.java")
                .withCheck(new AvoidGettingSizeCollectionInLoop())
                .verifyIssues();
    }

    @Test
    void testIgnoredWhileFoop() {
        CheckVerifier.newVerifier()
                .onFile("src/test/files/AvoidGettingSizeCollectionInWhileLoopIgnored.java")
                .withCheck(new AvoidGettingSizeCollectionInLoop())
                .verifyNoIssues();
    }

    @Test
    void testGoodWhileLoop() {
        CheckVerifier.newVerifier()
                .onFile("src/test/files/AvoidGettingSizeCollectionInWhileLoopGood.java")
                .withCheck(new AvoidGettingSizeCollectionInLoop())
                .verifyNoIssues();
    }

    @Test
    void testIgnoredForEachLoop() {
        CheckVerifier.newVerifier()
                .onFile("src/test/files/AvoidGettingSizeCollectionInForEachLoopIgnored.java")
                .withCheck(new AvoidGettingSizeCollectionInLoop())
                .verifyNoIssues();
    }
}
