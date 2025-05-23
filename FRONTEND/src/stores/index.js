import { defineStore } from "pinia";
import bootstrap from "bootstrap/dist/js/bootstrap.min.js";

export const useAppStore = defineStore("storeId", {
  state: () => ({
    bootstrap,
    identificador: '', // Almacena el id del usuario
    tipoPersona: '',   // Almacena el tipo de persona
    login: false,
    verificationCode: '',
    recoveryCode: null,
    email: '',
  }),

  actions: {
    setRecoveryCode(code) {
      this.recoveryCode = code;
    },
    setIdentificador(id) {
      this.identificador = id;
    },
    setTipoPersona(tipo) {
      this.tipoPersona = tipo;
    },
    setLogin(login) {
      this.login = login; 
    },
    setVerificationCode(verificationCode) {
      this.verificationCode = verificationCode; 
    },
    setEmail(email) {
      this.email = email;
    },
    clearSession() {
      this.identificador = null;
      this.tipoPersona = null;
    },
  },

  getters: {
    getIdentificador: (state) => state.identificador,
    getTipoPersona: (state) => state.tipoPersona,
    isLoggedIn: (state) => state.login,
    getVerificationCode: (state) => state.verificationCode,
    getEmail: (state) => state.email,
  },
});
