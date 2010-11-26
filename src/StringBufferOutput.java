
public class StringBufferOutput {

	public static void main(String[] args) {
		StringBuffer sql = new StringBuffer();
		sql.append("select tipo_viagem from solicitacao s ");
		sql.append(" inner join servico_aereo_sol sas on ");
		sql.append("       (s.id_solicitacao = sas.id_solicitacao) ");
		sql.append(" inner join servico_aereo_res sar on ");
		sql.append("       (sas.id_serv_aereo = sar.id_serv_aereo) ");
		sql.append(" where sar.id_serv_aereo_res = ");
        System.out.println(sql);
	}
}
