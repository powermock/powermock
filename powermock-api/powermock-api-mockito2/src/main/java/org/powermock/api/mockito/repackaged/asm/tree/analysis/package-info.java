/**
 * <p>
 * Provides a framework for static code analysis based on the asm.tree package.
 * </p>
 * <p>
 * Basic usage:
 * </p>
 * <pre>
 * ClassReader cr = new ClassReader(bytecode);
 * ClassNode cn = new ClassNode();
 * cr.accept(cn, ClassReader.SKIP_DEBUG);
 * List methods = cn.methods;
 * for (int i = 0; i < methods.size(); ++i) {
 * MethodNode method = (MethodNode) methods.get(i);
 * if (method.instructions.size() > 0) {
 * Analyzer a = new Analyzer(new BasicInterpreter());
 * a.analyze(cn.name, method);
 * Frame[] frames = a.getFrames();
 * // Elements of the frames arrray now contains info for each instruction
 * // from the analyzed method. BasicInterpreter creates BasicValue, that
 * // is using simplified type system that distinguishes the UNINITIALZED,
 * // INT, FLOAT, LONG, DOUBLE, REFERENCE and RETURNADDRESS types.
 * ...
 * }
 * }
 * </pre>
 * <p>
 * @since ASM 1.4.3
 * </p>
 */
package org.powermock.api.mockito.repackaged.asm.tree.analysis;