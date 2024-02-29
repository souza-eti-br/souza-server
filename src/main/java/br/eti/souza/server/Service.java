package br.eti.souza.server;

import br.eti.souza.exception.I18nMessage;
import br.eti.souza.exception.SystemException;
import br.eti.souza.exception.UserException;
import br.eti.souza.json.JSON;

/**
 * Abstração para manipular requisição HTTP.
 * @author Alan Moraes Souza
 */
public abstract class Service {

  /**
   * Escolhe o que executar com base no HTTP método.
   * @param request Requisição.
   * @return Resultado do processamento da requisição.
   * @throws SystemException Exceção causada pelo sistema.
   * @throws UserException Exceção causada pelo usuário.
   */
  protected Response execute(Request request) throws SystemException, UserException {
    if (Method.GET.equals(request.getMethod())) {
      return this.get(request);
    } else if (Method.HEAD.equals(request.getMethod())) {
      return this.head(request);
    } else if (Method.POST.equals(request.getMethod())) {
      return this.post(request);
    } else if (Method.PUT.equals(request.getMethod())) {
      return this.put(request);
    } else if (Method.DELETE.equals(request.getMethod())) {
      return this.delete(request);
    } else if (Method.CONNECT.equals(request.getMethod())) {
      return this.connect(request);
    } else if (Method.OPTIONS.equals(request.getMethod())) {
      return this.options(request);
    } else if (Method.TRACE.equals(request.getMethod())) {
      return this.trace(request);
    } else if (Method.PATCH.equals(request.getMethod())) {
      return this.patch(request);
    } else {
      var response = new Response(405, "Method Not Allowed");
      response.setContentType("text/json");
      response.setContentBody(JSON.toJSON(new I18nMessage("method.not.allowed", (request.getMethod() != null ? request.getMethod().toString() : "null"))));
      return response;
    }
  }

  /**
   * Processa o HTTP método GET.
   * @param request Requisição.
   * @return Resultado do processamento da requisição.
   * @throws SystemException Exceção causada pelo sistema.
   * @throws UserException Exceção causada pelo usuário.
   */
  protected Response get(Request request) throws SystemException, UserException {
    var response = new Response(501, "Not Implemented");
    response.setContentType("text/json");
    response.setContentBody(JSON.toJSON(new I18nMessage("not.implemented")));
    return response;
  }

  /**
   * Processa o HTTP método HEAD.
   * @param request Requisição.
   * @return Resultado do processamento da requisição.
   * @throws SystemException Exceção causada pelo sistema.
   * @throws UserException Exceção causada pelo usuário.
   */
  protected Response head(Request request) throws SystemException, UserException {
    var response = new Response(501, "Not Implemented");
    response.setContentType("text/json");
    response.setContentBody(JSON.toJSON(new I18nMessage("not.implemented")));
    return response;
  }

  /**
   * Processa o HTTP método POST.
   * @param request Requisição.
   * @return Resultado do processamento da requisição.
   * @throws SystemException Exceção causada pelo sistema.
   * @throws UserException Exceção causada pelo usuário.
   */
  protected Response post(Request request) throws SystemException, UserException {
    var response = new Response(501, "Not Implemented");
    response.setContentType("text/json");
    response.setContentBody(JSON.toJSON(new I18nMessage("not.implemented")));
    return response;
  }

  /**
   * Processa o HTTP método PUT.
   * @param request Requisição.
   * @return Resultado do processamento da requisição.
   * @throws SystemException Exceção causada pelo sistema.
   * @throws UserException Exceção causada pelo usuário.
   */
  protected Response put(Request request) throws SystemException, UserException {
    var response = new Response(501, "Not Implemented");
    response.setContentType("text/json");
    response.setContentBody(JSON.toJSON(new I18nMessage("not.implemented")));
    return response;
  }

  /**
   * Processa o HTTP método DELETE.
   * @param request Requisição.
   * @return Resultado do processamento da requisição.
   * @throws SystemException Exceção causada pelo sistema.
   * @throws UserException Exceção causada pelo usuário.
   */
  protected Response delete(Request request) throws SystemException, UserException {
    var response = new Response(501, "Not Implemented");
    response.setContentType("text/json");
    response.setContentBody(JSON.toJSON(new I18nMessage("not.implemented")));
    return response;
  }

  /**
   * Processa o HTTP método CONNECT.
   * @param request Requisição.
   * @return Resultado do processamento da requisição.
   * @throws SystemException Exceção causada pelo sistema.
   * @throws UserException Exceção causada pelo usuário.
   */
  protected Response connect(Request request) throws SystemException, UserException {
    var response = new Response(501, "Not Implemented");
    response.setContentType("text/json");
    response.setContentBody(JSON.toJSON(new I18nMessage("not.implemented")));
    return response;
  }

  /**
   * Processa o HTTP método OPTIONS.
   * @param request Requisição.
   * @return Resultado do processamento da requisição.
   * @throws SystemException Exceção causada pelo sistema.
   * @throws UserException Exceção causada pelo usuário.
   */
  protected Response options(Request request) throws SystemException, UserException {
    var response = new Response(501, "Not Implemented");
    response.setContentType("text/json");
    response.setContentBody(JSON.toJSON(new I18nMessage("not.implemented")));
    return response;
  }

  /**
   * Processa o HTTP método TRACE.
   * @param request Requisição.
   * @return Resultado do processamento da requisição.
   * @throws SystemException Exceção causada pelo sistema.
   * @throws UserException Exceção causada pelo usuário.
   */
  protected Response trace(Request request) throws SystemException, UserException {
    var response = new Response(501, "Not Implemented");
    response.setContentType("text/json");
    response.setContentBody(JSON.toJSON(new I18nMessage("not.implemented")));
    return response;
  }

  /**
   * Processa o HTTP método PATCH.
   * @param request Requisição.
   * @return Resultado do processamento da requisição.
   * @throws SystemException Exceção causada pelo sistema.
   * @throws UserException Exceção causada pelo usuário.
   */
  protected Response patch(Request request) throws SystemException, UserException {
    var response = new Response(501, "Not Implemented");
    response.setContentType("text/json");
    response.setContentBody(JSON.toJSON(new I18nMessage("not.implemented")));
    return response;
  }
}
