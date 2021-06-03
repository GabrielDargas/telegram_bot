package telegram_bot;

import java.util.List;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.ChatAction;
import com.pengrad.telegrambot.request.GetUpdates;
import com.pengrad.telegrambot.request.SendChatAction;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.BaseResponse;
import com.pengrad.telegrambot.response.GetUpdatesResponse;
import com.pengrad.telegrambot.response.SendResponse;

public class Main {

	public static void main(String[] args) {
		// Criacao do objeto bot com as informacoes de acesso.
		TelegramBot bot = new TelegramBot("");

		// Objeto responsavel por receber as mensagens.
		GetUpdatesResponse updatesResponse;

		// Objeto responsavel por gerenciar o envio de respostas.
		SendResponse sendResponse;

		// Objeto responsavel por gerenciar o envio de acoes do chat.
		BaseResponse baseResponse;

		// Controle de off-set, isto e, a partir deste ID sera lido as mensagens
		// pendentes na fila.
		int m = 0;

		// Loop infinito pode ser alterado por algum timer de intervalo curto.
		while (true) {
			// Executa comando no Telegram para obter as mensagens pendentes a partir de um
			// off-set (limite inicial).
			updatesResponse = bot.execute(new GetUpdates().limit(200).offset(m));

			// Lista de mensagens.
			List<Update> updates = updatesResponse.updates();

			// Analise de cada acao da mensagem.
			for (Update update : updates) {

				// Atualizacao do off-set.
				m = update.updateId() + 1;

				System.out.println("Recebendo mensagem: " + update.message().text());

				// Envio de "Escrevendo" antes de enviar a resposta.
				baseResponse = bot.execute(new SendChatAction(update.message().chat().id(), ChatAction.typing.name()));

				// Verificacao de acao de chat foi enviada com sucesso.
				System.out.println("Resposta de Chat Action Enviada? " + baseResponse.isOk());

				// Envio da mensagem de resposta
				
				//Envio de Resposta para possíveis cumprimentos. Envia uma lista do que o bot faz.
				if(update.message().text().contains("Olá") || update.message().text().contains("Oi")) {
					sendResponse = bot.execute(new SendMessage(update.message().chat().id(), "Olá, "+ update.message().from().firstName()  +", tudo bem?"
							 + "\nEu faço 4 coisas: "
							 + "\n1. Posso procurar por algum CEP, caso queira fazer isso, é só digitar o número de CEP que você quer consultar."
							 + "\n2. Atividade 2"
							 + "\n3. Atividade 3"
							 + "\n4. Atividade 4"));
					//Reconhece através do REGEX que é um CEP. Envia que o CEP foi reconhecido.
				} else if(update.message().text().matches("\\d{5}-\\d{3}") || update.message().text().matches("\\d{8}")){
					sendResponse = bot.execute(new SendMessage(update.message().chat().id(), "Aqui tem um CEP"));
					//Resposta padrão
				} else {
					sendResponse = bot.execute(new SendMessage(update.message().chat().id(), "Não entendi"));
				}
				

				// Verificacao de mensagem enviada com sucesso.
				System.out.println("Mensagem Enviada? " + sendResponse.isOk());
			}
		}
	}
}
