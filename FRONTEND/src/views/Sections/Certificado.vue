<template>
  <div id="certificate-container" class="certificate">
    <!-- Encabezado -->
    <div class="certificate-header">
      <h1>CERTIFICADO</h1>
      <p>POR COMPLETAR EL CURSO</p>
    </div>

    <!-- Cuerpo -->
    <div class="certificate-body">
      <p>El presente certificado se otorga a:</p>
      <h2 class="certificate-name">{{ name }}</h2>
      <p>Por cumplir satisfactoriamente el curso de <strong>{{ courseTitle }}</strong>.</p>
      <p>Lo felicita muy atentamente el equipo de <strong>ELEVATE</strong>.</p>
    </div>

    <!-- Footer -->
    <div class="certificate-footer">
      <div class="signature">
        <p>Luis Huanca</p>
        <span>Gerente General</span>
      </div>
      <div class="medal">
        🏅
      </div>
      <div class="signature">
        <p>Camilo Méndez</p>
        <span>Jefe Creativo</span>
      </div>
    </div>

    <!-- Botón de descarga -->
    <button class="download-btn" @click="downloadCertificate">Descargar Certificado</button>
  </div>
</template>

<script>
import jsPDF from "jspdf";
import html2canvas from "html2canvas";

export default {
  name: "Certificate",
  data() {
    return {
      courseTitle: this.$route.query.courseTitle || '',
      name: this.$route.query.name || '',
    };
  },
  methods: {
    async downloadCertificate() {
      const container = document.getElementById("certificate-container");
      const canvas = await html2canvas(container, { scale: 2 });
      const imgData = canvas.toDataURL("image/png");
      const pdf = new jsPDF("landscape", "mm", "a4");
      const width = pdf.internal.pageSize.getWidth();
      const height = pdf.internal.pageSize.getHeight();
      pdf.addImage(imgData, "PNG", 0, 0, width, height);
      pdf.save("certificado.pdf");
    },
  },
};
</script>

<style scoped>
/* Estilo general */
.certificate {
  width: 800px;
  margin: 0 auto;
  border: 5px solid #e0d6b9;
  border-radius: 20px;
  padding: 30px;
  font-family: 'Arial', sans-serif;
  text-align: center;
  background: linear-gradient(135deg, #f7f9fc, #ffffff);
  box-shadow: 0px 8px 15px rgba(0, 0, 0, 0.1);
}

/* Encabezado */
.certificate-header h1 {
  font-size: 3.5rem;
  color: #2c3e50;
  margin: 0;
}

.certificate-header p {
  font-size: 1.2rem;
  font-weight: bold;
  color: #34495e;
  margin-top: 0.5em;
}

/* Cuerpo */
.certificate-body {
  margin-top: 20px;
}

.certificate-body p {
  font-size: 1.2rem;
  color: #2c3e50;
}

.certificate-name {
  font-size: 2.5rem;
  font-family: 'Cursive', sans-serif;
  color: #34495e;
  margin: 20px 0;
}

/* Footer */
.certificate-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 40px;
}

.signature {
  text-align: center;
}

.signature p {
  font-weight: bold;
  font-size: 1rem;
  margin-bottom: 0.3rem;
}

.signature span {
  font-size: 0.9rem;
  color: #7f8c8d;
}

.medal {
  font-size: 3rem;
  color: gold;
}

/* Botón de descarga */
.download-btn {
  display: inline-block;
  margin-top: 20px;
  padding: 10px 20px;
  background: #3498db;
  color: #fff;
  border: none;
  border-radius: 5px;
  font-size: 1rem;
  cursor: pointer;
  transition: background 0.3s ease;
}

.download-btn:hover {
  background: #2980b9;
}
</style>