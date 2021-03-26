package br.eti.souza.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Estrutura de dados que representa uma part do Path, usada para organizar os handler no HandlerManager
 * @author Alan Moraes Souza
 */
public final class PathPart {

    /** Lista as próximas possíveis partes do Path, se for uma variável usa como path {varName}. */
    protected final Map<String, PathPart> pathParts = new HashMap<>();
    /** Lista dos nomes das variáveis correspondente aos Handlers deste pathPart. */
    protected final List<String> varNames = new ArrayList<>();
    /** Handler do path. */
    protected Handler handler = null;

    /** Construtor padrão */
    protected PathPart() {
    }
}
