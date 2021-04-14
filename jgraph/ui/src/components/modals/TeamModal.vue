<template>
  <b-modal
    :id="id"
    dialog-class="default-modal team-modal"
    centered
    scrollable
    @change="changed"
  >
    <template v-slot:modal-header="{ close }">
      <h5 class="text-sb">{{ team.name ? 'Edit Team' : 'Create Team' }}</h5>
      <b-icon-x @click="close"> </b-icon-x>
    </template>
    <b-row no-gutters class="mt-3">
      <b-form-group label="Name*" class="w-100">
        <b-form-input
          :state="validateState('name')"
          v-model="$v.form.name.$model"
          autofocus
        ></b-form-input>
        <b-form-invalid-feedback v-if="!$v.form.name.required"
          >Name must not be empty.</b-form-invalid-feedback
        >
      </b-form-group>
      <b-form-group label="Description" class="w-100">
        <WYSIWYG ref="WYSIWYG" :content="content" />
      </b-form-group>
    </b-row>

    <template v-slot:modal-footer>
      <b-button variant="outline-secondary" class="default-size" @click="close">
        Cancel
      </b-button>
      <b-button variant="primary" class="default-size" @click="save">
        {{ edit ? 'Save' : 'Create' }}
      </b-button>
    </template>
  </b-modal>
</template>

<script lang="ts">
import { Component, Vue, Watch } from 'vue-property-decorator';
import { validateState } from '@/utils/helpers';
import { required } from 'vuelidate/lib/validators';
import WYSIWYG from '@/components/WYSIWYG.vue';

Component.registerHooks(['validations']);

@Component({
  components: { WYSIWYG },
  methods: { validateState }
})
export default class TeamModal extends Vue {
  id = 'team-modal';
  form = {
    name: '',
    description: ''
  };
  content = '';
  edit = false;

  get team() {
    return this.$store.state.team.team;
  }

  @Watch('$store.state.team.team', { deep: true })
  onTeamChange() {
    this.initTeam();
  }

  validations() {
    return {
      form: {
        name: {
          required
        }
      }
    };
  }

  mounted() {}

  changed() {
    this.initTeam();
  }

  initTeam() {
    this.edit = this.$store.state.team.team.name ? true : false;
    this.form = Object.assign({}, this.$store.state.team.team);
    this.content = this.form.description;
  }

  async save() {
    this.$v.form.$touch();
    if (!this.$v.form.$invalid) {
      this.form.description = (this.$refs['WYSIWYG'] as any).editor.getHTML();
      let response = await this.$store.dispatch('updateTeam', this.form);
      if (response.status === 200) {
        this.close();
        this.$v.$reset();
      }
    }
  }

  close() {
    this.$bvModal.hide(this.id);
  }

  open() {
    this.$bvModal.show(this.id);
  }

  beforeDestroy() {}
}
</script>

<style lang="scss">
.team-modal {
  width: 570px;
  max-width: 100%;
  textarea {
    min-height: 100px;
  }
}
</style>
