package Conexao;

public class Funcionario extends Usuario {

    private String cargo;
    private String departamento;

    // Construtor
    public Funcionario(int id, String nome, String tipoUsuario, String cargo, String departamento) {
        super(id, nome, tipoUsuario); // Chama o construtor da classe pai (Usuario)
        this.cargo = cargo;
        this.departamento = departamento;
    }

    // Getters e Setters
    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public String getDepartamento() {
        return departamento;
    }

    public void setDepartamento(String departamento) {
        this.departamento = departamento;
    }

    // Método para exibir as informações do funcionário (inclui as informações do usuário)
    @Override
    public void exibirInformacoes() {
        super.exibirInformacoes(); // Chama o método exibirInformacoes() da classe pai (Usuario)
        System.out.println("Cargo: " + cargo);
        System.out.println("Departamento: " + departamento);
    }

    // Método para adicionar um novo funcionário ao sistema
    public static void adicionarFuncionario(int id, String nome, String tipoUsuario, String cargo, String departamento) {
        // Aqui você pode adicionar lógica para adicionar o funcionário ao banco de dados
        System.out.println("Funcionário " + nome + " do cargo " + cargo + " adicionado com sucesso!");
    }

    // Método para listar todos os funcionários (poderia ser implementado para buscar do banco de dados)
    public static void listarFuncionarios() {
        // Aqui você pode adicionar a lógica para buscar os funcionários do banco de dados
        System.out.println("Listando todos os funcionários...");
        // Exemplo de exibição
        System.out.println("ID: 1, Nome: Carlos, Cargo: Bibliotecário, Departamento: Gestão de Acervos");
        System.out.println("ID: 2, Nome: Ana, Cargo: Assistente, Departamento: Atendimento ao Público");
    }
}
