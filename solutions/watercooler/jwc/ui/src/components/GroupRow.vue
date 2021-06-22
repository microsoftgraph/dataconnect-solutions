<template>
  <div class="group-row-wrapper">
    <b-card>
      <b-avatar
        button
        @click="viewGroup"
        :text="initials"
        :style="{
          backgroundColor: getRandomColor(
            group.displayName.charCodeAt(group.displayName.length - 1)
          )
        }"
      ></b-avatar>
      <span class="name" @click="viewGroup">
        <span class="fw-500">{{ group.displayName }}</span>
        <span class="rate" v-if="group.attendanceRate">{{
          `${group.attendanceRate}% Attended`
        }}</span>
      </span>
      <span @click="viewMembers" class="ml-auto icons-members-wrapper">
        <span class="mr-2 text-secondary">
          <span class="mr-1">{{ group.members.length }}</span
          >{{ group.members.length === 1 ? 'Member' : 'Members' }}</span
        >
        <b-avatar-group size="38px">
          <b-avatar
            v-for="member in group.members"
            :src="'data:image/jpeg;base64,' + member.image"
            :key="`group_${group.id}_${member.email}`"
          ></b-avatar>
        </b-avatar-group>
      </span>
    </b-card>
  </div>
</template>

<script lang="ts">
// eslint-disable-next-line no-unused-vars
import { DayGroup } from '@/types/Groups';
import { Component, Emit, Prop, Vue } from 'vue-property-decorator';
import { getRandomColor } from '@/shared/helpers';

@Component({
  components: {},
  methods: {
    getRandomColor
  }
})
export default class GroupRow extends Vue {
  colors = ['#ffe494'];
  @Prop({
    default: () => {
      return {};
    }
  })
  group!: DayGroup;

  @Emit('viewGroup')
  viewGroup() {}

  @Emit('viewMembers')
  viewMembers() {}

  mounted() {}

  get initials() {
    let match = this.group.displayName?.match(/\b\w/g) || [];
    let initials = ((match.shift() || '') + (match.pop() || '')).toUpperCase();
    return initials;
  }
}
</script>

<style lang="scss">
.group-row-wrapper {
  flex: 1;
  .card {
    border-radius: 8px;
    .card-body {
      display: flex;
      padding: 12px;
      align-items: center;
      .b-avatar {
        color: black;
        margin-right: 12px;
      }
      .name {
        cursor: pointer;
        display: flex;
        flex-direction: column;
        justify-content: center;
        .rate {
          font-size: 12px;
          color: $secondary;
        }
      }
      .icons-members-wrapper {
        cursor: pointer;
        display: flex;
        align-items: center;
        .b-avatar-group {
          .b-avatar {
            border: 3px solid white;
            min-width: 38px;
          }
        }
      }
    }
  }
}
</style>
