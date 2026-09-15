package br.com.chatbank.kit.core.config;

import br.com.chatbank.kit.mockbank.MockBankConfiguration;
import br.com.chatbank.kit.usecases.extrato.ConsultarExtratoTool;
import br.com.chatbank.kit.usecases.saldo.ConsultarSaldoTool;
import br.com.chatbank.kit.usecases.ui.RenderExtratoTool;
import br.com.chatbank.kit.usecases.ui.RenderSaldoTool;
import br.com.chatbank.kit.usecases.ui.UiWidgetResources;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Import;

@AutoConfiguration
@Import({
    MockBankConfiguration.class,
    ConsultarSaldoTool.class,
    ConsultarExtratoTool.class,
    RenderSaldoTool.class,
    RenderExtratoTool.class,
    UiWidgetResources.class
})
public class KitAutoConfiguration {
}
