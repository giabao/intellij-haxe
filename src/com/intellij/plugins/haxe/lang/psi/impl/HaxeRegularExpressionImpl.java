package com.intellij.plugins.haxe.lang.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.plugins.haxe.lang.psi.HaxeRegularExpression;
import com.intellij.plugins.haxe.lang.psi.HaxeVarDeclarationPart;
import com.intellij.plugins.haxe.lang.psi.HaxeVarInit;
import com.intellij.plugins.haxe.util.HaxeElementGenerator;
import com.intellij.psi.LiteralTextEscaper;
import com.intellij.psi.PsiLanguageInjectionHost;
import org.intellij.lang.regexp.psi.RegExpGroup;
import org.jetbrains.annotations.NotNull;

/**
 * @author: Fedor.Korotkov
 */
public class HaxeRegularExpressionImpl extends HaxeReferenceImpl implements HaxeRegularExpression {
  public HaxeRegularExpressionImpl(@NotNull ASTNode node) {
    super(node);
  }

  @Override
  public boolean isValidHost() {
    return true;
  }

  @Override
  public PsiLanguageInjectionHost updateText(@NotNull String text) {
    ASTNode node = getNode();
    ASTNode parent = node.getTreeParent();
    final HaxeVarDeclarationPart varDeclarationPart = HaxeElementGenerator.createVarDeclarationPart(getProject(), "a=" + text);
    final HaxeVarInit varInit = varDeclarationPart.getVarInit();
    final ASTNode outerNode = varInit == null ? null : varInit.getNode();
    assert outerNode != null;
    parent.replaceChild(node, outerNode);

    return (PsiLanguageInjectionHost)outerNode.getPsi();
  }

  @NotNull
  @Override
  public LiteralTextEscaper<? extends PsiLanguageInjectionHost> createLiteralTextEscaper() {
    return new LiteralTextEscaper<HaxeRegularExpression>(this) {
      @Override
      public boolean decode(@NotNull TextRange rangeInsideHost, @NotNull StringBuilder outChars) {
        outChars.append(myHost.getText(), rangeInsideHost.getStartOffset(), rangeInsideHost.getEndOffset());
        return true;
      }

      @Override
      public int getOffsetInHost(int offsetInDecoded, @NotNull TextRange rangeInsideHost) {
        int offset = offsetInDecoded + rangeInsideHost.getStartOffset();
        if (offset < rangeInsideHost.getStartOffset()) offset = rangeInsideHost.getStartOffset();
        if (offset > rangeInsideHost.getEndOffset()) offset = rangeInsideHost.getEndOffset();
        return offset;
      }

      @Override
      public boolean isOneLine() {
        return true;
      }
    };
  }

  @Override
  public boolean characterNeedsEscaping(char c) {
    return false;
  }

  @Override
  public boolean supportsPerl5EmbeddedComments() {
    return false;
  }

  @Override
  public boolean supportsPossessiveQuantifiers() {
    return false;
  }

  @Override
  public boolean supportsPythonConditionalRefs() {
    return false;
  }

  @Override
  public boolean supportsNamedGroupSyntax(RegExpGroup group) {
    return false;
  }
}